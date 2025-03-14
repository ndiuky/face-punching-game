package engine.render;


import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform1iv;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20C.glGetAttribLocation;
import static org.lwjgl.opengl.GL20C.glUniform2fv;
import static org.lwjgl.opengl.GL20C.glUniform3fv;
import static org.lwjgl.opengl.GL20C.glUniform4fv;
import static org.lwjgl.opengl.GL20C.glUniformMatrix2fv;

public class Shader {

    private int shaderProgramID;

    private String vertexSource;

    private String fragmentSource;

    private String filepath;

    private boolean beingUsed = false;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: Could not open file for shader: '" + filepath + "'");
        }
    }

    public void compile() {
        int vertexID, fragmentID;

        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);

            throw new RuntimeException("ERROR: '" + filepath + "'\n\tVertex shader compilation failed. " + glGetShaderInfoLog(vertexID, len));
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);

            throw new RuntimeException("ERROR: '" + filepath + "'\n\tFragment shader compilation failed. " + glGetShaderInfoLog(fragmentID, len));
        }

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);

            throw new RuntimeException("ERROR: '" + filepath + "'\n\tLinking of shaders failed. " + glGetShaderInfoLog(shaderProgramID, len));
        }
    }

    public void use() {
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }

    public void uploadIntArray(String varName, int[] val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        uploadInt(varName, slot);
    }

    public int getShaderProgramID() {
        return shaderProgramID;
    }

    /**
     * Gets the location of an attribute variable with specified name.
     *
     * @param name Attribute name
     *
     * @return Location of the attribute
     */
    public int getAttributeLocation(CharSequence name) {
        return glGetAttribLocation(shaderProgramID, name);
    }

    public int getUniformLocation(CharSequence name) {
        return glGetUniformLocation(shaderProgramID, name);
    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value    Value to set
     */
    public void setUniform(int location, int value) {
        glUniform1i(location, value);
    }

//    /**
//     * Sets the uniform variable for specified location.
//     *
//     * @param location Uniform location
//     * @param value    Value to set
//     */
//    public void setUniform(int location, Vector2f value) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            FloatBuffer buffer = stack.mallocFloat(2);
//            value.toBuffer(buffer);
//            glUniform2fv(location, buffer);
//        }
//    }
//
//    /**
//     * Sets the uniform variable for specified location.
//     *
//     * @param location Uniform location
//     * @param value    Value to set
//     */
//    public void setUniform(int location, Vector3f value) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            FloatBuffer buffer = stack.mallocFloat(3);
//            value.toBuffer(buffer);
//            glUniform3fv(location, buffer);
//        }
//    }
//
//    /**
//     * Sets the uniform variable for specified location.
//     *
//     * @param location Uniform location
//     * @param value    Value to set
//     */
//    public void setUniform(int location, Vector4f value) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            FloatBuffer buffer = stack.mallocFloat(4);
//            value.toBuffer(buffer);
//            glUniform4fv(location, buffer);
//        }
//    }
//
//    /**
//     * Sets the uniform variable for specified location.
//     *
//     * @param location Uniform location
//     * @param value    Value to set
//     */
//    public void setUniform(int location, Matrix2f value) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            FloatBuffer buffer = stack.mallocFloat(2 * 2);
//            value.toBuffer(buffer);
//            glUniformMatrix2fv(location, false, buffer);
//        }
//    }
//
//    /**
//     * Sets the uniform variable for specified location.
//     *
//     * @param location Uniform location
//     * @param value    Value to set
//     */
//    public void setUniform(int location, Matrix3f value) {
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            FloatBuffer buffer = stack.mallocFloat(3 * 3);
//            value.toBuffer(buffer);
//            glUniformMatrix3fv(location, false, buffer);
//        }
//    }

    /**
     * Sets the uniform variable for specified location.
     *
     * @param location Uniform location
     * @param value    Value to set
     */
    public void setUniform(int location, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            buffer.put(value.m00()).put(value.m10()).put(value.m20()).put(value.m30());
            buffer.put(value.m01()).put(value.m11()).put(value.m21()).put(value.m31());
            buffer.put(value.m02()).put(value.m12()).put(value.m22()).put(value.m32());
            buffer.put(value.m03()).put(value.m13()).put(value.m23()).put(value.m33());
            buffer.flip();
            glUniformMatrix4fv(location, false, buffer);
        }
    }

}
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
import java.util.Objects;

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

public class Shader {

    public final int shaderProgramID;

    private boolean beingUsed = false;

    // TODO: парсинг некрасивый, переделай на какие-нибудь мапки и листы
    public Shader(String filepath) {
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            String firstPattern = source.substring(index, source.indexOf("\r\n", index)).trim();

            String fragmentSource, vertexSource;
            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
                fragmentSource = splitString[2];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
                vertexSource = splitString[2];
            } else {
                throw new RuntimeException("Unexpected token '" + firstPattern + "'");
            }

            int vertexID = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexID, vertexSource);
            glCompileShader(vertexID);

            if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new RuntimeException("ERROR: '" + filepath + "'\n\tVertex shader compilation failed. " + glGetShaderInfoLog(vertexID, glGetShaderi(vertexID, GL_INFO_LOG_LENGTH)));
            }

            int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentID, fragmentSource);
            glCompileShader(fragmentID);

            if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new RuntimeException("ERROR: '" + filepath + "'\n\tFragment shader compilation failed. " + glGetShaderInfoLog(fragmentID, glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH)));
            }

            shaderProgramID = glCreateProgram();
            glAttachShader(shaderProgramID, vertexID);
            glAttachShader(shaderProgramID, fragmentID);
            glLinkProgram(shaderProgramID);

            if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE) {
                throw new RuntimeException("ERROR: '" + filepath + "'\n\tLinking of shaders failed. " + glGetShaderInfoLog(shaderProgramID, glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH)));
            }

        } catch (IOException e) {
            throw new RuntimeException("Error: Could not open file for shader: '" + filepath + "'");
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
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1i(varLocation, val);
    }

    public void uploadIntArray(String varName, int[] val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        glUniform1iv(varLocation, val);
    }

    public void uploadTexture(String varName, int slot) {
        uploadInt(varName, slot);
    }

    public int getShaderProgramID() {
        return shaderProgramID;
    }

    public int getAttributeLocation(CharSequence name) {
        return glGetAttribLocation(shaderProgramID, name);
    }

    public int getUniformLocation(CharSequence name) {
        return glGetUniformLocation(shaderProgramID, name);
    }

    public void setUniform(int location, int value) {
        glUniform1i(location, value);
    }

    //TODO: переделать на загрузку обычных массивов
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shader shader)) return false;
        return shaderProgramID == shader.shaderProgramID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(shaderProgramID);
    }
}
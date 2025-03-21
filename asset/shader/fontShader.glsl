#type vertex
#version 330 core

layout (location=0) in vec2 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTextureCoords;

out vec4 vertexColor;
out vec2 textureCoord;

uniform mat4 uView;
uniform mat4 uProjection;

void main() {
    vertexColor = aColor;
    textureCoord = aTextureCoords;
    gl_Position = uProjection * uView * vec4(aPos, 0.0, 1.0);
}

#type fragment
#version 330 core

in vec4 vertexColor;
in vec2 textureCoord;

out vec4 fragColor;

uniform sampler2D uTexture;

void main() {
    vec4 textureColor = texture(uTexture, textureCoord);
    fragColor = vertexColor * textureColor;
}

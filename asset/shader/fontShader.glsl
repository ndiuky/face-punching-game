#type vertex
#version 330 core

in vec2 position;
in vec4 color;
in vec2 texcoord;

out vec4 vertexColor;
out vec2 textureCoord;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProjection;

void main() {
    vertexColor = color;
    textureCoord = texcoord;
    mat4 mvp = uProjection * uView * uModel;
    gl_Position = mvp * vec4(position, 0.0, 1.0);
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

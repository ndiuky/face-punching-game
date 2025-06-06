#type vertex
#version 330 core

layout (location=0) in vec2 aPos;
//TODO: не думаю что тут нужен цвет, возможно стоит поменять на обычный float для представления прозрачности
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTextureCoords;

out vec4 fColor;
out vec2 fTextureCoords;

void main(){
    gl_Position = vec4(aPos, 1, 1);

    fColor = aColor;
    fTextureCoords = aTextureCoords;
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTextureCoords;

uniform sampler2D uTexture;

out vec4 color;

void main(){
    color = fColor * texture(uTexture, fTextureCoords);
}

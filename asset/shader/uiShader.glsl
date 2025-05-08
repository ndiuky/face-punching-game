#type vertex
#version 330 core

layout (location=0) in vec2 aPos;
//TODO: не думаю что тут нужен цвет, возможно стоит поменять на обычный float для представления прозрачности
layout (location=1) in vec4 aColor;
layout (location=2) in float aTextureId;

out vec4 fColor;
out float fTextureId;
out vec2 fTextureCoords;

void main(){
    gl_Position = vec4(aPos, 1, 1);

    fColor = aColor;
    fTextureId = aTextureId;

    // TODO: возможно стоит передавать фиксированные текстурные координаты прямо с CPU, а на стороне GPU не заниматься вычислениями
    int vertexId = gl_VertexID % 4;
    if (vertexId == 0){
        fTextureCoords = vec2(0, 1);
    } else if (vertexId == 1){
        fTextureCoords = vec2(0, 0);
    } else if (vertexId == 2){
        fTextureCoords = vec2(-1, 0);
    } else {
        fTextureCoords = vec2(-1, 1);
    }
}

#type fragment
#version 330 core

in vec4 fColor;
in float fTextureId;
in vec2 fTextureCoords;

uniform sampler2D uTextures[8];

out vec4 color;

void main(){
    if (fTextureId > 0) {
        color = fColor * texture(uTextures[int(fTextureId)], fTextureCoords);
    } else {
        color = fColor;
    }
}

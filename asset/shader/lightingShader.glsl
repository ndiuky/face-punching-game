#type vertex
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTextureCoords;
layout (location=3) in float aTextureId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec4 vCol;

void main(){
    fColor = aColor;
    vec2 vertices[3] = vec2[3](vec2(-1, -1), vec2(3, -1), vec2(-1, 3));
    gl_Position = vec4(vertices[gl_VertexID], 0, 1);
    vCol = (gl_Position + 1) / 2;
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTextureCoords;
in float fTextureId;
in vec4 vCol;

uniform sampler2D uTextures[8];

out vec4 color;

void main(){
    float radius = 100;
    if (pow(abs(gl_FragCoord.x - 500), 2) + pow(abs(gl_FragCoord.y - 250), 2) > pow(radius, 2)){
        color = vCol;
    } else {
        color = vec4(0, 0, 0, 1);
    }

    //    color = vec4(vCol.xyz * cos(gl_FragCoord.x * 0.1) * tan(gl_FragCoord.x * 0.1) * sin(gl_FragCoord.x * 0.1) * cos(gl_FragCoord.y * 0.1) + 0.4, 1);

    //    color = vec4(vCol.xyz * sin((gl_FragCoord.x * gl_FragCoord.y) * 0.1) * cos((gl_FragCoord.x * gl_FragCoord.y) * 0.1), 1);
}

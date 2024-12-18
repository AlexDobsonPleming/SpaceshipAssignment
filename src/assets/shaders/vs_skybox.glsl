#version 330 core
// Alexander Dobson-Pleming
// adobson-pleming1@sheffield.ac.uk
// I declare that this is my own work
layout (location = 0) in vec3 aPos;

out vec3 TexCoords;

uniform mat4 projection;
uniform mat4 view;

void main()
{
    TexCoords = aPos;
    gl_Position = projection * view * vec4(aPos, 1.0);
}
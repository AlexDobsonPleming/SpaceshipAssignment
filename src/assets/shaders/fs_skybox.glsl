#version 330 core
// Alexander Dobson-Pleming
// adobson-pleming1@sheffield.ac.uk
// I declare that this is my own work
out vec4 FragColor;

in vec3 TexCoords;

uniform samplerCube skybox;

void main()
{
    FragColor = texture(skybox, TexCoords);
//    FragColor = vec4(1.0, 0.0, 0.0, 1.0); // Render red
}
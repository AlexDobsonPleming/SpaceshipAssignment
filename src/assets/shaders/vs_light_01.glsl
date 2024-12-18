#version 330 core
// credit: from the labs
layout (location = 0) in vec3 position;

uniform mat4 mvpMatrix;

void main() {
  gl_Position = mvpMatrix * vec4(position, 1.0);
}
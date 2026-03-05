#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    color.rgb += color.rgb * 1.5;
    color.rgb *= vec3(1.0, 0.9, 0.4);
}

#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    color.rgb = floor(color.rgb * 4.0) / 4.0;
    color.rgb *= 0.5;
}

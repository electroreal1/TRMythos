#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    float r = texture(DiffuseSampler, texCoord + Offeset).r;

    float g = texture(DiffuseSampler, texture).g;

    float b = texture(DiffuseSampler, texCoord - Offeset);

    fragColor = vec4(r, g, b, 1);
}

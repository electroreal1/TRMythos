#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    float d = distance(texCoord, vec2(0.5));

    color.rgb = mix(color.rgb, vec3(0.7, 0.9, 1.0), d * 0.8);
}

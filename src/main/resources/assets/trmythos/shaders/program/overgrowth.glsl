#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    vec4 neighbor = texture(DiffuseSampler, texCoord + vec2(0.001, 0.001));

    float edge = distance(color.rgb, neighbor.rgb);

    color.rgb = mix(color.rgb, vec3(0.0, 1.0, 0.0), edge * 5.0);
}

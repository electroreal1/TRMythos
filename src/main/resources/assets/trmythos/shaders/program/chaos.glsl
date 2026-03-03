#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    float hue = Time * 2.0;

    color.rgb = mix(vec3(dot(color.rgb, vec3(0.333))), color.rgb, cos(hue));
}

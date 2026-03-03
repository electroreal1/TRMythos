#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    float dist = distance(texCoord, vec2(0.5, 0.5));

    color.rgb *= 1.0 - smoothstep(0.2, 0.6, dist);

    color.r += dist * 0.5 * (sin(Time * 2.0) + 1.0);
}

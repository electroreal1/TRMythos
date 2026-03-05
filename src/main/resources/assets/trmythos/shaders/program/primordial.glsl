#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {

    vec2 uv = texCoord;

    uv.y += sin(uv.x * 20.0 + Time) * 0.01;

    color = texture(DiffuseSampler, uv) * vec4(1.0, 0.6, 0.0, 1.0);
}

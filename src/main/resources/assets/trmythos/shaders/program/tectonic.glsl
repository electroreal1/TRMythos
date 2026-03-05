#version 120
uniform sampler2D DiffuseSampler;
uniform float Time;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    float shift = sin(texCoord.y * 10.0 + Time * 2.0) * 0.02;

    vec2 distortedUV = vec2(texCoord.x + shift, texCoord.y);

    vec4 color = texture(DiffuseSampler, distortedUV);

    color.rgb *= vec3(1.1, 1.0, 0.9);

    fragColor = color;
}

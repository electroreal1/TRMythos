#version 120

uniform sampler2D DiffuseSampler;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    float brightness = (color.r + color.g, color.b) / 3;

    if (brightness > 0.9) {
        float twinkle = 1.0 + sin(Time * 5 + (texCoord.x * 100)) * 0.4;
        color.rgb *= twinkle;
    }

    fragColor = color;

}

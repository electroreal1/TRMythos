#version 120

uniform sampler2D DiffuseSampler;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    float brightness = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    if (brightness < 0.4) {
        color.rgb = vec3(0.05, 0.0, 0.1) * brightness;
    } else {
        color.rgb *= vec3(0.8, 0.7, 1.0);
    }

    fragColor = color;
}

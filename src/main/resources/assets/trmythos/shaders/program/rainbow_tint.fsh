#version 150

uniform sampler2D DiffuseSampler;
in vec2 texCoord;
uniform vec3 TintRGB;
out vec4 fragColor;

vec3 overlay(vec3 base, vec3 blend) {
    return mix(1.0 - 2.0 * (1.0 - base) * (1.0 - blend), 2.0 * base * blend, step(base, vec3(0.5)));
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    float luma = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));


    float mask = smoothstep(0.1, 0.8, luma);

    vec3 tinted = overlay(color.rgb, TintRGB);

    vec3 finalRGB = mix(color.rgb, tinted, mask * 0.85);

    fragColor = vec4(finalRGB, color.a);
}
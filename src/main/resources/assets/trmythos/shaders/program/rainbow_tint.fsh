#version 110

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;

uniform vec3 TintRGB;
uniform float Time;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);

    float luminance = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));

    float pulse = (sin(Time * 1.2) * 0.1) + 0.9;

    vec3 tinted = mix(color.rgb, (TintRGB * luminance) * pulse, 0.85);
    gl_FragColor = vec4(tinted * TintRGB, color.a);

    float dist = distance(texCoord, vec2(0.5, 0.5));
    float vignette = smoothstep(0.7, 0.3, dist);

    gl_FragColor = vec4(tinted * vignette, color.a);
}
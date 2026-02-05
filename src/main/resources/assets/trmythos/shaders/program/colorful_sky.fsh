#version 110

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;

uniform vec3 TintRGB;
uniform float Time;

void main() {
    vec4 color = texture2D(DiffuseSampler, texCoord);

    float luminance = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    float pulse = (sin(Time * 1.5) * 0.05) + 0.95;

    vec3 tintedColor = mix(color.rgb, TintRGB * luminance * pulse, 0.5);

    float dist = distance(texCoord, vec2(0.5, 0.5));
    float vignette = smoothstep(0.8, 0.4, dist);

    gl_FragColor = vec4(tintedColor * vignette, color.a);
}
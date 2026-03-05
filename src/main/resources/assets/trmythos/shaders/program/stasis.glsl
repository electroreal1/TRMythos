#version 120

uniform sampler2D DiffuseSampler;
uniform vec2 Offeset;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    
    float lum = dot(color.rgb, vec3(0.3, 0.59, 0.11));
    
    float noise = fract(sin(dot(texCoord ,vec2(12.9898,78.233))) * 43758.5453);
    
    color.rgb = vec3(lum) + (noise * 0.1);
}

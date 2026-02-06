#version 150
uniform sampler2D Sampler0;
uniform vec3 TintColor;
in vec4 vertexColor;
in vec2 texCoord0;
out vec4 fragColor;

void main() {
    vec4 base = texture(Sampler0, texCoord0) * vertexColor;
    fragColor = vec4(base.rgb * TintColor, base.a);
}
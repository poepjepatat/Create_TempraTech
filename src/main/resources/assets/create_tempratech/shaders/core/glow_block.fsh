#version 150

uniform sampler2D Sampler0;

uniform float GlowStrength;
uniform vec3 GlowColor;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;


void main() {

    vec4 textureColor =
    texture(Sampler0, texCoord0);

    vec3 baseColor =
    textureColor.rgb *
    vertexColor.rgb;


    /*
     * Emission.
     *
     * GlowStrength:
     *
     * 0.0  = none
     * 0.1  = subtle
     * 1.0  = strong
     * 2.0  = very strong
     * 10.0 = extreme
     */
    vec3 emission =
    baseColor *
    GlowColor *
    GlowStrength;


    vec3 finalColor =
    baseColor +
    emission;


    fragColor = vec4(
            finalColor,
            textureColor.a *
            vertexColor.a
    );
}
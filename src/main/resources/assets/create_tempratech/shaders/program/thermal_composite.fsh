#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D HeatSampler;
uniform sampler2D RawHeatSampler;
uniform vec2 InSize;
uniform float Time;

in vec2 texCoord;
out vec4 fragColor;

float intensity(vec4 value) {
    return clamp(max(value.a, max(value.r, max(value.g, value.b))), 0.0, 1.0);
}

void main() {
    vec2 texel = 1.0 / max(InSize, vec2(1.0));
    vec4 blurredHeat = texture(HeatSampler, texCoord);
    vec4 rawHeat = texture(RawHeatSampler, texCoord);
    float heat = intensity(blurredHeat);
    float core = intensity(rawHeat);

    float left = intensity(texture(HeatSampler, texCoord - vec2(texel.x * 2.0, 0.0)));
    float right = intensity(texture(HeatSampler, texCoord + vec2(texel.x * 2.0, 0.0)));
    float down = intensity(texture(HeatSampler, texCoord - vec2(0.0, texel.y * 2.0)));
    float up = intensity(texture(HeatSampler, texCoord + vec2(0.0, texel.y * 2.0)));
    vec2 heatGradient = vec2(right - left, up - down);

    float waveX = sin(texCoord.y * 760.0 + Time * 70.0)
            + 0.5 * sin(texCoord.y * 1330.0 - Time * 47.0);
    float waveY = sin(texCoord.x * 610.0 - Time * 53.0);
    vec2 shimmer = vec2(waveX, waveY) * 0.00065;
    vec2 distortion = (heatGradient * 0.006 + shimmer) * heat;

    vec2 uv = clamp(texCoord + distortion, vec2(0.0), vec2(1.0));
    float chroma = 0.0012 * heat;
    vec3 scene;
    scene.r = texture(DiffuseSampler, clamp(uv + vec2(chroma, 0.0), vec2(0.0), vec2(1.0))).r;
    scene.g = texture(DiffuseSampler, uv).g;
    scene.b = texture(DiffuseSampler, clamp(uv - vec2(chroma, 0.0), vec2(0.0), vec2(1.0))).b;

    // Keep a strong, crisp contribution from the block mask so the glow
    // follows the voxel shape instead of becoming a softened cube.
    vec3 bloom = blurredHeat.rgb * (0.8 + 2.4 * heat);
    vec3 whiteHot = mix(rawHeat.rgb, vec3(1.0, 0.94, 0.78), core * 0.7) * core * 1.15;

    fragColor = vec4(scene + bloom + whiteHot, texture(DiffuseSampler, uv).a);
}

package Mods.create_tempratech.Client.Glowing;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;

public final class GlowShader {

    private static ShaderInstance shader;

    private GlowShader() {}

    public static void setShader(ShaderInstance instance) {
        shader = instance;
    }

    public static ShaderInstance getShader() {
        return shader;
    }

    public static void setStrength(float strength) {
        if (shader == null) {
            return;
        }

        Uniform uniform = shader.getUniform("GlowStrength");

        if (uniform != null) {
            uniform.set(strength);
        }
    }

    public static void setColor(
            float red,
            float green,
            float blue
    ) {
        if (shader == null) {
            return;
        }

        Uniform uniform = shader.getUniform("GlowColor");

        if (uniform != null) {
            uniform.set(red, green, blue);
        }
    }
}
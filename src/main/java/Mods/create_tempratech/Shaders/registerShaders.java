package Mods.create_tempratech.Shaders;

import Mods.create_tempratech.Client.Glowing.GlowShader;
import Mods.create_tempratech.Create_tempratech;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public class registerShaders {
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        try {
            ShaderInstance shader = new ShaderInstance(
                    event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath(
                            Create_tempratech.MODID,
                            "glow_block"
                    ),
                    DefaultVertexFormat.POSITION_TEX_COLOR
            );

            event.registerShader(shader, GlowShader::setShader);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load glow_block shader",
                    e
            );
        }
    }
}

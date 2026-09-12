package Mods.create_tempratech.Client;

import Mods.create_tempratech.Client.Glowing.GlowRenderer;
import Mods.create_tempratech.Client.Renderers.ActiveLiquidVentRenderer;
import Mods.create_tempratech.Client.Renderers.CustomPipeAttachmentModel;
import Mods.create_tempratech.Client.Renderers.ElectroMagneticPipeRenderer;
import Mods.create_tempratech.Client.Renderers.ReinforcedPipeRenderer;
import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.modBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import static Mods.create_tempratech.Create_tempratech.LOGGER;

@EventBusSubscriber(
        modid = Create_tempratech.MODID,
        value = Dist.CLIENT
) public class ClientRegister {

    private static final boolean CUSTOM_PIPE_PARTIALS =
            CustomPipeAttachmentModel.initialize();

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {
        LOGGER.info("REGISTERING HEAT PIPE RENDERER");



        event.registerBlockEntityRenderer(
                modBlockEntities.ACTIVE_LIQUID_VENT_ENTITY.get(),
                ActiveLiquidVentRenderer::new
        );
        event.registerBlockEntityRenderer(
                modBlockEntities.REINFORCED_PIPE_ENTITY.get(),
                ReinforcedPipeRenderer::new
        );
        event.registerBlockEntityRenderer(
                modBlockEntities.ELECTRO_MAGNETIC_PIPE.get(),
                ElectroMagneticPipeRenderer::new
        );
    }

    @SubscribeEvent
    public static void wrapPipeModels(ModelEvent.ModifyBakingResult event) {
        for (var entry : event.getModels().entrySet()) {
            var location = entry.getKey();
            String path = location.id().getPath();
            if (!location.id().getNamespace().equals(
                    Create_tempratech.MODID)
                    || (!path.equals("reinforced_pipe")
                    && !path.equals("electromagnetic_pipe"))) {
                continue;
            }

            entry.setValue(CustomPipeAttachmentModel.withAO(
                    entry.getValue(),
                    path.equals("reinforced_pipe")
                            ? "reinforced_pipe"
                            : "electromagnetic_pipe"));
        }
    }

    public static void registerFluidExtensions(
            RegisterClientExtensionsEvent event
    ) {
        ResourceLocation stillTexture =
                ResourceLocation.withDefaultNamespace("block/lava_still");
        ResourceLocation flowingTexture =
                ResourceLocation.withDefaultNamespace("block/lava_flow");
        ;
    }

    public static void registerReloadListeners(
            RegisterClientReloadListenersEvent event
    ) {
        event.registerReloadListener((ResourceManagerReloadListener)
                resourceManager -> GlowRenderer.invalidatePostChain());
    }
}

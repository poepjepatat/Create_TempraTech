package Mods.create_tempratech.Client;

import Mods.create_tempratech.Client.Renderers.HeatHarvesterRenderer;
import Mods.create_tempratech.Client.Renderers.HeatPipeRenderer;
import Mods.create_tempratech.Client.Renderers.ThermometerRenderer;
import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.modBlockEntities;
import Mods.create_tempratech.Regs.modFluids;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import static Mods.create_tempratech.Create_tempratech.LOGGER;

@EventBusSubscriber(
        modid = Create_tempratech.MODID,
        value = Dist.CLIENT
) public class ClientRegister {

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {
        LOGGER.info("REGISTERING HEAT PIPE RENDERER");

        event.registerBlockEntityRenderer(
                modBlockEntities.HEAT_PIPE_ENTITY.get(),
                HeatPipeRenderer::new
        );

        event.registerBlockEntityRenderer(
                modBlockEntities.THERMOMETER_ENTITY.get(),
                ThermometerRenderer::new
        );

        event.registerBlockEntityRenderer(
                modBlockEntities.HEAT_HARVESTER_ENTITY.get(),
                HeatHarvesterRenderer::new
        );
    }

    public static void registerFluidExtensions(
            RegisterClientExtensionsEvent event
    ) {
        ResourceLocation stillTexture =
                ResourceLocation.withDefaultNamespace("block/lava_still");
        ResourceLocation flowingTexture =
                ResourceLocation.withDefaultNamespace("block/lava_flow");

        modFluids.all().forEach(fluid -> event.registerFluidType(
                new IClientFluidTypeExtensions() {
                    @Override
                    public ResourceLocation getStillTexture() {
                        return stillTexture;
                    }

                    @Override
                    public ResourceLocation getFlowingTexture() {
                        return flowingTexture;
                    }

                    @Override
                    public int getTintColor() {
                        return fluid.tintColor();
                    }
                },
                fluid.type().get()
        ));
    }
}
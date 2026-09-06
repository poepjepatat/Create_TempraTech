package Mods.create_tempratech.Client;

import Mods.create_tempratech.Client.Renderers.HeatPipeRenderer;
import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.modBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import static Mods.create_tempratech.Create_tempratech.LOGGER;

@EventBusSubscriber(
        modid = Create_tempratech.MODID,
        value = Dist.CLIENT,
        bus = Bus.MOD
)
public class ClientRegister {

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {
        LOGGER.info("REGISTERING HEAT PIPE RENDERER");

        event.registerBlockEntityRenderer(
                modBlockEntities.HEAT_PIPE_ENTITY.get(),
                HeatPipeRenderer::new
        );
    }
}
package Mods.create_tempratech.Server;

import Mods.create_tempratech.Create_tempratech;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(
        modid = Create_tempratech.MODID
)
public final class ModCommands {

    private ModCommands() {}

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        AdminCommands.register(event);
    }
}

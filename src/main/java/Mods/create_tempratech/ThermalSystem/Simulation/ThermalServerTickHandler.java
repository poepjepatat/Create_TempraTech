package Mods.create_tempratech.ThermalSystem.Simulation;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class ThermalServerTickHandler {

    private ThermalServerTickHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();

        for (ServerLevel level : server.getAllLevels()) {
            ThermalSimulationManager.tick(level);
        }
    }
}
package Mods.create_tempratech.ThermalSystem.Simulation;

import Mods.create_tempratech.Client.Glowing.GlowManager;
import Mods.create_tempratech.Network.ThermalGlowPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerLevel level = event.getLevel();
        ChunkPos watchedChunk = event.getPos();

        GlowManager.forEach(level, (pos, data) -> {
            if (!new ChunkPos(pos).equals(watchedChunk)) {
                return;
            }

            PacketDistributor.sendToPlayer(
                    event.getPlayer(),
                    new ThermalGlowPayload(
                            pos.asLong(),
                            data.strength(),
                            data.red(),
                            data.green(),
                            data.blue()
                    )
            );
        });
    }
}
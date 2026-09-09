package Mods.create_tempratech.ThermalSystem.Simulation;

import Mods.create_tempratech.Client.Glowing.GlowManager;
import Mods.create_tempratech.Config;
import Mods.create_tempratech.Network.ThermalGlowPayload;
import Mods.create_tempratech.Network.ThermalVisualPayload;
import Mods.create_tempratech.Regs.Items.VisualThermalGogglesItem;
import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public final class ThermalServerTickHandler {

    private ThermalServerTickHandler() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();

        for (ServerLevel level : server.getAllLevels()) {
            ThermalSimulationManager.tick(level);

            for (ServerPlayer player : level.players()) {
                if (player.tickCount % Config.visualScanIntervalTicks == 0
                        && player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD)
                        .getItem() instanceof VisualThermalGogglesItem) {
                    sendVisualSnapshot(level, player);
                }
            }
        }
    }

    private static void sendVisualSnapshot(
            ServerLevel level,
            ServerPlayer player
    ) {
        BlockPos center = player.blockPosition();
        List<ThermalVisualPayload.Entry> blocks = new ArrayList<>();
        int horizontalRadius = Config.visualScanHorizontalRadius;
        int verticalRadius = Config.visualScanVerticalRadius;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-horizontalRadius, -verticalRadius, -horizontalRadius),
                center.offset(horizontalRadius, verticalRadius, horizontalRadius)
        )) {
            if (!level.hasChunkAt(pos)
                    || level.getBlockState(pos).isAir()) {
                continue;
            }

            blocks.add(new ThermalVisualPayload.Entry(
                    pos.asLong(),
                    (float) ThermalWorld.getTemperatureCelsius(level, pos)
            ));

            if (blocks.size() >= Config.maxVisualScanEntries) {
                break;
            }
        }

        PacketDistributor.sendToPlayer(
                player,
                new ThermalVisualPayload(blocks)
        );
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
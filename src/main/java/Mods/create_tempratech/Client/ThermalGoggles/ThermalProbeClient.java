package Mods.create_tempratech.Client.ThermalGoggles;

import Mods.create_tempratech.Network.ThermalProbeRequestPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ThermalProbeClient {

    private static long lastRequestedPos = Long.MIN_VALUE;
    private static long lastRequestTick = Long.MIN_VALUE;

    private ThermalProbeClient() {
    }

    public static void request(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        long gameTime = minecraft.level.getGameTime();
        long packedPos = pos.asLong();

        if (packedPos == lastRequestedPos && gameTime - lastRequestTick < 5L) {
            return;
        }

        lastRequestedPos = packedPos;
        lastRequestTick = gameTime;
        PacketDistributor.sendToServer(new ThermalProbeRequestPayload(packedPos));
    }
}

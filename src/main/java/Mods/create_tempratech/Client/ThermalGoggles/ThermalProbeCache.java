package Mods.create_tempratech.Client.ThermalGoggles;

import net.minecraft.core.BlockPos;

import java.util.OptionalDouble;

public final class ThermalProbeCache {

    private static long packedPos = Long.MIN_VALUE;
    private static double temperatureC;
    private static long receivedAtNanos;

    private ThermalProbeCache() {
    }

    public static synchronized void accept(BlockPos pos, double temperature) {
        packedPos = pos.asLong();
        temperatureC = temperature;
        receivedAtNanos = System.nanoTime();
    }

    public static synchronized OptionalDouble get(BlockPos pos) {
        if (packedPos != pos.asLong()) {
            return OptionalDouble.empty();
        }

        if (System.nanoTime() - receivedAtNanos > 1_000_000_000L) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(temperatureC);
    }

    public static synchronized void clear() {
        packedPos = Long.MIN_VALUE;
        receivedAtNanos = 0L;
    }
}

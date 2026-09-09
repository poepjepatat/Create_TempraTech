package Mods.create_tempratech.Client.Glowing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public final class VisualThermalManager {

    private static final Map<Level, Map<BlockPos, GlowManager.GlowData>> DATA =
            new WeakHashMap<>();

    private VisualThermalManager() {
    }

    public static void setSnapshot(
            Level level,
            Map<BlockPos, GlowManager.GlowData> snapshot
    ) {
        DATA.put(level, new HashMap<>(snapshot));
    }

    public static void clear(Level level) {
        DATA.remove(level);
    }

    public static void forEach(
            Level level,
            BiConsumer<BlockPos, GlowManager.GlowData> consumer
    ) {
        Map<BlockPos, GlowManager.GlowData> worldData = DATA.get(level);
        if (worldData != null) {
            worldData.forEach(consumer);
        }
    }
}

package Mods.create_tempratech.Client.Glowing;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public final class GlowManager {

    private GlowManager() {}

    /*
     * Glow settings are stored per world and per BlockPos.
     *
     * This means:
     *
     * (100, 64, 100) -> strength 5
     * (101, 64, 100) -> strength 0
     *
     * even if both blocks are the exact same block.
     */
    private static final Map<Level, Map<BlockPos, GlowData>> GLOW_DATA =
            new WeakHashMap<>();

    public static void setGlow(Level level, BlockPos pos, float strength) {
        setGlow(level, pos, strength, 1.0F, 1.0F, 1.0F);
    }

    public static void setGlow(
            Level level,
            BlockPos pos,
            float strength,
            float red,
            float green,
            float blue
    ) {
        if (level == null || pos == null) {
            return;
        }

        strength = Math.max(0.0F, strength);

        if (strength == 0.0F) {
            removeGlow(level, pos);
            return;
        }

        GlowData data = new GlowData(
                strength,
                clamp01(red),
                clamp01(green),
                clamp01(blue)
        );

        GLOW_DATA
                .computeIfAbsent(level, ignored -> new HashMap<>())
                .put(pos.immutable(), data);
    }

    public static GlowData getGlow(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return null;
        }

        Map<BlockPos, GlowData> worldData = GLOW_DATA.get(level);

        if (worldData == null) {
            return null;
        }

        return worldData.get(pos);
    }

    public static boolean isGlowing(Level level, BlockPos pos) {
        GlowData data = getGlow(level, pos);

        return data != null && data.strength() > 0.0F;
    }

    public static float getStrength(Level level, BlockPos pos) {
        GlowData data = getGlow(level, pos);

        if (data == null) {
            return 0.0F;
        }

        return data.strength();
    }

    public static void removeGlow(Level level, BlockPos pos) {
        if (level == null || pos == null) {
            return;
        }

        Map<BlockPos, GlowData> worldData = GLOW_DATA.get(level);

        if (worldData != null) {
            worldData.remove(pos);

            if (worldData.isEmpty()) {
                GLOW_DATA.remove(level);
            }
        }
    }

    public static void forEach(
            Level level,
            BiConsumer<BlockPos, GlowData> consumer
    ) {
        if (level == null || consumer == null) {
            return;
        }

        Map<BlockPos, GlowData> worldData = GLOW_DATA.get(level);

        if (worldData != null) {
            worldData.forEach(consumer);
        }
    }

    public static void clear(Level level) {
        if (level != null) {
            GLOW_DATA.remove(level);
        }
    }

    public static void clearAll() {
        GLOW_DATA.clear();
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    public record GlowData(
            float strength,
            float red,
            float green,
            float blue
    ) {}
}
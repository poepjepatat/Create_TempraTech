package Mods.create_tempratech.ThermalSystem.Simulation;

import Mods.create_tempratech.Regs.modBlocks;
import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ThermalTransformations {

    public static final double IRON_MELTING_POINT_C = 1538.0;
    public static final double IRON_SOLIDIFY_POINT_C = 1500.0;
    public static final double GOLD_MELTING_POINT_C = 1064.0;
    public static final double GOLD_SOLIDIFY_POINT_C = 1020.0;
    public static final double LAVA_BASALT_POINT_C = 700.0;

    private ThermalTransformations() {
    }

    /**
     * Applies temperature-driven block transformations while preserving the
     * block's absolute temperature across the material change.
     */
    public static boolean tryTransform(
            ServerLevel level,
            BlockPos pos,
            double temperatureC
    ) {
        BlockState state = level.getBlockState(pos);
        BlockState replacement = null;

        if (state.is(Blocks.IRON_BLOCK)
                && temperatureC >= IRON_MELTING_POINT_C) {
            replacement = modBlocks.MOLTEN_IRON.get().defaultBlockState();
        } else if (state.is(modBlocks.MOLTEN_IRON.get())
                && temperatureC <= IRON_SOLIDIFY_POINT_C) {
            replacement = Blocks.IRON_BLOCK.defaultBlockState();
        } else if (state.is(Blocks.GOLD_BLOCK)
                && temperatureC >= GOLD_MELTING_POINT_C) {
            replacement = modBlocks.MOLTEN_GOLD.get().defaultBlockState();
        } else if (state.is(modBlocks.MOLTEN_GOLD.get())
                && temperatureC <= GOLD_SOLIDIFY_POINT_C) {
            replacement = Blocks.GOLD_BLOCK.defaultBlockState();
        } else if (state.is(Blocks.LAVA)
                && temperatureC <= LAVA_BASALT_POINT_C) {
            replacement = Blocks.BASALT.defaultBlockState();
        }

        if (replacement == null) {
            return false;
        }

        if (!level.setBlockAndUpdate(pos, replacement)) {
            return false;
        }

        ThermalWorld.setTemperatureCelsius(level, pos, temperatureC);
        ThermalSimulationManager.activateBlockAndNeighbors(level, pos);
        return true;
    }
}

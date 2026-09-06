package Mods.create_tempratech.ThermalSystem;

import Mods.create_tempratech.Regs.ModAttachments;
import Mods.create_tempratech.ThermalSystem.Simulation.ThermalSimulationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ThermalWorld {

    /*
     * For now, assume one Minecraft block is:
     *
     * 1 m × 1 m × 1 m
     *
     * Therefore:
     *
     * volume = 1 m³
     */
    public static final double BLOCK_VOLUME_M3 = 1.0;

    private ThermalWorld() {
    }

    /**
     * Gets the thermal data for a chunk.
     */
    private static ThermalChunkData getChunkData(
            Level level,
            BlockPos pos
    ) {
        LevelChunk chunk = level.getChunkAt(pos);

        return chunk.getData(
                ModAttachments.THERMAL_DATA
        );
    }

    /**
     * Gets stored energy for a block.
     *
     * A block with no stored energy has 0 J relative
     * to its default temperature.
     */
    public static double getEnergy(
            Level level,
            BlockPos pos
    ) {
        ThermalChunkData data =
                getChunkData(level, pos);

        return data.getEnergy(pos);
    }

    /**
     * Sets energy for a block.
     */
    public static void setEnergy(
            ServerLevel level,
            BlockPos pos,
            double joules
    ) {
        LevelChunk chunk =
                level.getChunkAt(pos);

        ThermalChunkData data =
                chunk.getData(
                        ModAttachments.THERMAL_DATA
                );

        data.setEnergy(pos, joules);

        /*
         * The attachment was mutated, so the chunk
         * needs to be marked dirty.
         */
        chunk.setUnsaved(true);

        activateForSimulation(level, pos);
    }

    /**
     * Adds energy to a block.
     */
    public static void addEnergy(
            ServerLevel level,
            BlockPos pos,
            double joules
    ) {
        LevelChunk chunk =
                level.getChunkAt(pos);

        ThermalChunkData data =
                chunk.getData(
                        ModAttachments.THERMAL_DATA
                );

        data.addEnergy(pos, joules);

        chunk.setUnsaved(true);

        activateForSimulation(level, pos);
    }

    /**
     * Gets the thermal material of a block.
     */
    public static ThermalMaterial getMaterial(
            Level level,
            BlockPos pos
    ) {
        Block block =
                level.getBlockState(pos).getBlock();

        return ThermalMaterials.getMaterial(block);
    }

    /**
     * Gets the default temperature of a block.
     */
    public static double getDefaultTemperature(
            ServerLevel level,
            BlockPos pos
    ) {
        ThermalMaterial material =
                getMaterial(level, pos);

        return material.defaultTemperatureK();
    }

    /**
     * Gets the current temperature of a block.
     *
     * Temperature is returned in Kelvin.
     */
    public static double getTemperature(
            Level level,
            BlockPos pos
    ) {
        ThermalMaterial material =
                getMaterial(level, pos);

        double joules =
                getEnergy(level, pos);

        return material.temperatureFromEnergy(
                joules,
                BLOCK_VOLUME_M3
        );
    }

    /**
     * Gets the current temperature in Celsius.
     */
    public static double getTemperatureCelsius(
            Level level,
            BlockPos pos
    ) {
        return getTemperature(level, pos) - 273.15;
    }

    /**
     * Sets the temperature of a block.
     *
     * The appropriate amount of energy is calculated
     * automatically.
     */
    public static void setTemperature(
            ServerLevel level,
            BlockPos pos,
            double temperatureK
    ) {
        ThermalMaterial material =
                getMaterial(level, pos);

        double joules =
                material.energyFromTemperature(
                        temperatureK,
                        BLOCK_VOLUME_M3
                );

        setEnergy(level, pos, joules);
    }

    /**
     * Sets the temperature in Celsius.
     */
    public static void setTemperatureCelsius(
            ServerLevel level,
            BlockPos pos,
            double temperatureC
    ) {
        setTemperature(
                level,
                pos,
                temperatureC + 273.15
        );
    }

    /**
     * Returns true if this block has custom
     * thermal energy stored.
     */
    public static boolean hasEnergyData(
            ServerLevel level,
            BlockPos pos
    ) {
        LevelChunk chunk =
                level.getChunkAt(pos);

        if (!chunk.hasData(
                ModAttachments.THERMAL_DATA
        )) {
            return false;
        }

        ThermalChunkData data =
                chunk.getData(
                        ModAttachments.THERMAL_DATA
                );

        return data.hasEnergyData(pos);
    }

    /**
     * A simulator only processes positions that have been explicitly queued.
     * Queue a block whenever its stored energy changes so its next server tick
     * can exchange heat with all six adjacent blocks.
     */
    private static void activateForSimulation(
            ServerLevel level,
            BlockPos pos
    ) {
        ThermalSimulationManager.activateBlockAndNeighbors(level, pos);
    }
}

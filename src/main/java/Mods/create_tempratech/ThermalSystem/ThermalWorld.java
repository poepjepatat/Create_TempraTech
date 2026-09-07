package Mods.create_tempratech.ThermalSystem;

import Mods.create_tempratech.Regs.ModAttachments;
import Mods.create_tempratech.ThermalSystem.Simulation.ThermalSimulationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ThermalWorld {

    public static final double BLOCK_VOLUME_M3 = 1.0;

    private ThermalWorld() {
    }

    private static ThermalChunkData getChunkData(
            Level level,
            BlockPos pos
    ) {
        LevelChunk chunk = level.getChunkAt(pos);
        return chunk.getData(ModAttachments.THERMAL_DATA);
    }

    public static double getEnergy(Level level, BlockPos pos) {
        ThermalChunkData data = getChunkData(level, pos);
        return data.getEnergy(pos);
    }

    public static double getGlowTemperature(Level level, BlockPos pos) {
        return getMaterial(level, pos).glowTemperature();
    }

    public static void setEnergy(
            ServerLevel level,
            BlockPos pos,
            double joules
    ) {
        LevelChunk chunk = level.getChunkAt(pos);
        ThermalChunkData data = chunk.getData(ModAttachments.THERMAL_DATA);
        data.setEnergy(pos, joules);
        chunk.setUnsaved(true);
        activateForSimulation(level, pos);
    }

    public static void addEnergy(
            ServerLevel level,
            BlockPos pos,
            double joules
    ) {
        LevelChunk chunk = level.getChunkAt(pos);
        ThermalChunkData data = chunk.getData(ModAttachments.THERMAL_DATA);
        data.addEnergy(pos, joules);
        chunk.setUnsaved(true);
        activateForSimulation(level, pos);
    }

    public static ThermalMaterial getMaterial(Level level, BlockPos pos) {
        return ThermalMaterials.getMaterial(level.getBlockState(pos));
    }

    public static double getDefaultTemperature(
            ServerLevel level,
            BlockPos pos
    ) {
        return getMaterial(level, pos).defaultTemperatureK();
    }

    public static double getTemperature(Level level, BlockPos pos) {
        ThermalMaterial material = getMaterial(level, pos);
        return material.temperatureFromEnergy(
                getEnergy(level, pos),
                BLOCK_VOLUME_M3
        );
    }

    public static double getTemperatureCelsius(Level level, BlockPos pos) {
        return getTemperature(level, pos) - 273.15;
    }

    public static void setTemperature(
            ServerLevel level,
            BlockPos pos,
            double temperatureK
    ) {
        ThermalMaterial material = getMaterial(level, pos);
        setEnergy(
                level,
                pos,
                material.energyFromTemperature(temperatureK, BLOCK_VOLUME_M3)
        );
    }

    public static void setTemperatureCelsius(
            ServerLevel level,
            BlockPos pos,
            double temperatureC
    ) {
        setTemperature(level, pos, temperatureC + 273.15);
    }

    public static boolean hasEnergyData(ServerLevel level, BlockPos pos) {
        LevelChunk chunk = level.getChunkAt(pos);

        if (!chunk.hasData(ModAttachments.THERMAL_DATA)) {
            return false;
        }

        ThermalChunkData data = chunk.getData(ModAttachments.THERMAL_DATA);
        return data.hasEnergyData(pos);
    }

    private static void activateForSimulation(
            ServerLevel level,
            BlockPos pos
    ) {
        ThermalSimulationManager.activateBlockAndNeighbors(level, pos);
    }
}

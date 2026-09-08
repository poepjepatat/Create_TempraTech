package Mods.create_tempratech.ThermalSystem.Simulation;

import Mods.create_tempratech.Client.Glowing.GlowManager;
import Mods.create_tempratech.Network.ThermalGlowPayload;
import Mods.create_tempratech.ThermalSystem.ThermalHeatSource;
import Mods.create_tempratech.ThermalSystem.ThermalMaterial;
import Mods.create_tempratech.ThermalSystem.ThermalMaterials;
import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;

public final class ThermalSimulator {

    public static final double DELTA_TIME = 1.0 / 20.0;
    public static final double CONTACT_AREA = 1.0;
    public static final double BLOCK_DISTANCE = 1.0;
    public static final double MIN_ENERGY_TRANSFER = 1.0;

    /** Effective natural-convection transfer used at a solid/air boundary. */
    public static final double AIR_SURFACE_TRANSFER_COEFFICIENT = 8.0;
    public static final double MIN_AIR_ENERGY_TRANSFER = 0.02;
    public static final double AIR_DIFFUSION_MULTIPLIER = 4.0;

    public static final int MAX_OPERATIONS_PER_TICK = 10_000;

    private static final double GLOW_HEAT_RANGE_C = 1500.0;
    private static final float GLOW_SYNC_EPSILON = 0.02F;

    private final ThermalQueue queue;
    private final ThermalActiveSet activeSet;

    public ThermalSimulator() {
        this.queue = new ThermalQueue();
        this.activeSet = new ThermalActiveSet();
    }

    public void activate(BlockPos pos) {
        long packedPos = pos.asLong();
        if (activeSet.activate(packedPos)) {
            queue.add(packedPos);
        }
    }

    public void tick(ServerLevel level) {
        int operations = 0;
        Set<Long> activeHeatSources = new HashSet<>();

        while (!queue.isEmpty() && operations < MAX_OPERATIONS_PER_TICK) {
            long packedPos = queue.poll();
            activeSet.deactivate(packedPos);
            BlockPos pos = BlockPos.of(packedPos);

            if (!level.hasChunkAt(pos)) {
                operations++;
                continue;
            }

            if (!level.getBlockState(pos).isAir()) {
                ThermalTransformations.tryTransform(
                        level,
                        pos,
                        ThermalWorld.getTemperatureCelsius(level, pos)
                );
            }

            BlockState state = level.getBlockState(pos);
            if (ThermalMaterials.getActiveHeatSource(state) != null) {
                activeHeatSources.add(packedPos);
            }

            updateGlow(level, pos);
            simulateBlock(level, pos);
            operations++;
        }

        /*
         * Heat sources are applied after the conduction queue has finished.
         * ThermalWorld.addEnergy() activates the source and its neighbors, so
         * doing this here guarantees one source injection per game tick rather
         * than repeatedly injecting while the same queue is being processed.
         */
        for (long packedPos : activeHeatSources) {
            applyHeatSource(level, BlockPos.of(packedPos));
        }
    }

    private void applyHeatSource(
            ServerLevel level,
            BlockPos pos
    ) {
        if (!level.hasChunkAt(pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        ThermalHeatSource heatSource =
                ThermalMaterials.getActiveHeatSource(state);

        if (heatSource == null) {
            return;
        }

        ThermalMaterial material =
                ThermalWorld.getMaterial(level, pos);
        double currentEnergy =
                ThermalWorld.getEnergy(level, pos);
        double targetEnergy =
                material.energyFromTemperature(
                        heatSource.maxTemperatureK(),
                        ThermalWorld.BLOCK_VOLUME_M3
                );
        double remainingEnergy =
                targetEnergy - currentEnergy;

        if (remainingEnergy > 0.0) {
            double generatedThisTick =
                    heatSource.powerWatts() * DELTA_TIME;
            double generatedEnergy =
                    Math.min(
                            generatedThisTick,
                            remainingEnergy
                    );

            if (generatedEnergy > 0.0) {
                ThermalWorld.addEnergy(
                        level,
                        pos,
                        generatedEnergy
                );
            }
        }

        /*
         * Keep a currently active heater alive even when it has reached its
         * target temperature, so it can replace heat lost to surrounding
         * blocks and air on later ticks.
         */
        activate(pos);
    }

    private void updateGlow(ServerLevel level, BlockPos pos) {
        GlowManager.GlowData previous = GlowManager.getGlow(level, pos);

        if (level.getBlockState(pos).isAir()) {
            clearGlow(level, pos, previous);
            return;
        }

        double temperatureC = ThermalWorld.getTemperatureCelsius(level, pos);
        double glowTemperatureC = ThermalWorld.getGlowTemperature(level, pos);

        if (temperatureC <= glowTemperatureC) {
            clearGlow(level, pos, previous);
            return;
        }

        float heat = (float) Math.max(
                0.0,
                Math.min(1.0, (temperatureC - glowTemperatureC) / GLOW_HEAT_RANGE_C)
        );
        float strength = 0.15F + 0.85F * (float) Math.sqrt(heat);

        float red;
        float green;
        float blue;

        if (heat < 1.0F / 3.0F) {
            float local = heat * 3.0F;
            red = 1.0F;
            green = lerp(0.9F, 0.1F, local);
            blue = 0.0F;
        } else if (heat < 2.0F / 3.0F) {
            float local = (heat - 1.0F / 3.0F) * 3.0F;
            red = lerp(1.0F, 0.1F, local);
            green = lerp(0.1F, 1.0F, local);
            blue = lerp(0.0F, 0.1F, local);
        } else {
            float local = (heat - 2.0F / 3.0F) * 3.0F;
            red = 0.1F;
            green = lerp(1.0F, 0.35F, local);
            blue = lerp(0.1F, 1.0F, local);
        }

        if (!hasMeaningfulGlowChange(previous, strength, red, green, blue)) {
            return;
        }

        GlowManager.setGlow(level, pos, strength, red, green, blue);
        setActualLight(level, pos, Math.max(1, Math.round(strength * 15.0F)));

        PacketDistributor.sendToPlayersTrackingChunk(
                level,
                new ChunkPos(pos),
                new ThermalGlowPayload(pos.asLong(), strength, red, green, blue)
        );
    }

    private void clearGlow(
            ServerLevel level,
            BlockPos pos,
            GlowManager.GlowData previous
    ) {
        setActualLight(level, pos, 0);

        if (previous == null) {
            return;
        }

        GlowManager.removeGlow(level, pos);
        PacketDistributor.sendToPlayersTrackingChunk(
                level,
                new ChunkPos(pos),
                new ThermalGlowPayload(pos.asLong(), 0.0F, 0.0F, 0.0F, 0.0F)
        );
    }

    private static void setActualLight(
            ServerLevel level,
            BlockPos pos,
            int lightLevel
    ) {
        AuxiliaryLightManager lightManager =
                ((IBlockGetterExtension) level).getAuxLightManager(pos);

        if (lightManager == null) {
            return;
        }

        int clamped = Math.max(0, Math.min(15, lightLevel));
        if (lightManager.getLightAt(pos) != clamped) {
            lightManager.setLightAt(pos, clamped);
        }
    }

    private static boolean hasMeaningfulGlowChange(
            GlowManager.GlowData previous,
            float strength,
            float red,
            float green,
            float blue
    ) {
        if (previous == null) {
            return true;
        }

        return Math.abs(previous.strength() - strength) > GLOW_SYNC_EPSILON
                || Math.abs(previous.red() - red) > GLOW_SYNC_EPSILON
                || Math.abs(previous.green() - green) > GLOW_SYNC_EPSILON
                || Math.abs(previous.blue() - blue) > GLOW_SYNC_EPSILON;
    }

    private static float lerp(float start, float end, float amount) {
        return start + (end - start) * amount;
    }

    private void simulateBlock(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) {
            return;
        }

        simulateNeighbor(level, pos, Direction.EAST);
        simulateNeighbor(level, pos, Direction.WEST);
        simulateNeighbor(level, pos, Direction.UP);
        simulateNeighbor(level, pos, Direction.DOWN);
        simulateNeighbor(level, pos, Direction.SOUTH);
        simulateNeighbor(level, pos, Direction.NORTH);
    }

    private void simulateNeighbor(
            ServerLevel level,
            BlockPos firstPos,
            Direction direction
    ) {
        BlockPos secondPos = firstPos.relative(direction);

        if (!level.hasChunkAt(secondPos)) {
            return;
        }

        BlockState firstState = level.getBlockState(firstPos);
        BlockState secondState = level.getBlockState(secondPos);
        boolean firstIsAir = firstState.isAir();
        boolean secondIsAir = secondState.isAir();

        double firstTemperature = ThermalWorld.getTemperature(level, firstPos);
        double secondTemperature = ThermalWorld.getTemperature(level, secondPos);
        double deltaTemperature = firstTemperature - secondTemperature;

        if (Math.abs(deltaTemperature) < 0.000001) {
            return;
        }

        ThermalMaterial firstMaterial = ThermalWorld.getMaterial(level, firstPos);
        ThermalMaterial secondMaterial = ThermalWorld.getMaterial(level, secondPos);
        double k1 = firstMaterial.conductivity();
        double k2 = secondMaterial.conductivity();
        double effectiveConductivity;

        if (firstIsAir && secondIsAir) {
            effectiveConductivity = Math.max(k1, k2) * AIR_DIFFUSION_MULTIPLIER;
        } else if (firstIsAir || secondIsAir) {
            effectiveConductivity = AIR_SURFACE_TRANSFER_COEFFICIENT;
        } else {
            if (k1 <= 0.0 || k2 <= 0.0) {
                return;
            }
            effectiveConductivity = (2.0 * k1 * k2) / (k1 + k2);
        }

        double power = effectiveConductivity
                * CONTACT_AREA
                / BLOCK_DISTANCE
                * deltaTemperature;
        double energyTransfer = power * DELTA_TIME;
        double minimumTransfer = firstIsAir || secondIsAir
                ? MIN_AIR_ENERGY_TRANSFER
                : MIN_ENERGY_TRANSFER;

        if (Math.abs(energyTransfer) < minimumTransfer) {
            return;
        }

        if (energyTransfer > 0.0) {
            transferEnergy(level, firstPos, secondPos, energyTransfer);
        } else {
            transferEnergy(level, secondPos, firstPos, -energyTransfer);
        }
    }

    private void transferEnergy(
            ServerLevel level,
            BlockPos source,
            BlockPos destination,
            double joules
    ) {
        if (joules <= 0.0) {
            return;
        }

        double sourceEnergy = ThermalWorld.getEnergy(level, source);
        double actualTransfer = joules;
        double maximumStableTransfer = Math.abs(sourceEnergy) * 0.25;

        if (sourceEnergy > 0.0) {
            actualTransfer = Math.min(actualTransfer, maximumStableTransfer);
        }

        if (actualTransfer <= 0.0) {
            return;
        }

        ThermalWorld.addEnergy(level, source, -actualTransfer);
        ThermalWorld.addEnergy(level, destination, actualTransfer);
        activate(source);
        activate(destination);
    }
}

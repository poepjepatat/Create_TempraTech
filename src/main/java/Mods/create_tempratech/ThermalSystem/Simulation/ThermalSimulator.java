package Mods.create_tempratech.ThermalSystem.Simulation;


import Mods.create_tempratech.Client.Glowing.GlowClient;
import Mods.create_tempratech.Client.Glowing.GlowManager;
import Mods.create_tempratech.ThermalSystem.ThermalMaterial;
import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

/**
 * Main thermal simulation.
 *
 * First version:
 *
 * - conduction only
 * - six neighboring blocks
 * - joules are the stored energy
 * - temperature is calculated from energy
 */
public final class ThermalSimulator {

    /**
     * Simulation timestep in seconds.
     *
     * Minecraft runs at 20 ticks per second,
     * so one tick = 0.05 seconds.
     */
    public static final double DELTA_TIME = 1.0 / 20.0;

    /**
     * Contact area between two Minecraft blocks.
     *
     * One Minecraft block is initially treated as
     * 1 m × 1 m × 1 m.
     */
    public static final double CONTACT_AREA = 1.0;

    /**
     * Distance between the centers of neighboring blocks.
     */
    public static final double BLOCK_DISTANCE = 1.0;

    /**
     * Don't bother transferring insignificant amounts
     * of energy.
     */
    public static final double MIN_ENERGY_TRANSFER = 1;

    /**
     * Maximum number of blocks simulated per tick.
     *
     * This prevents a huge thermal event from freezing
     * the Minecraft server.
     */
    public static final int MAX_OPERATIONS_PER_TICK = 10_000;

    private final ThermalQueue queue;
    private final ThermalActiveSet activeSet;

    public ThermalSimulator() {
        this.queue = new ThermalQueue();
        this.activeSet = new ThermalActiveSet();
    }

    /**
     * Adds a block to the thermal simulation.
     */
    public void activate(BlockPos pos) {
        long packedPos = pos.asLong();

        if (activeSet.activate(packedPos)) {
            queue.add(packedPos);
        }
    }

    /**
     * Processes thermal simulation for one server tick.
     */
    public void tick(ServerLevel level) {

        int operations = 0;

        while (
                !queue.isEmpty()
                        && operations < MAX_OPERATIONS_PER_TICK
        ) {

            long packedPos = queue.poll();

            activeSet.deactivate(packedPos);

            BlockPos pos = BlockPos.of(packedPos);

            double Temperature = ThermalWorld.getTemperatureCelsius(level, pos);
            double glowTemperature = ThermalWorld.getGlowTemperature(level, pos);
            double glowStrength = 0;

            if(Temperature > glowTemperature){
                double glowTemp = Temperature - glowTemperature;

                glowStrength = glowTemp / 10000;
            }

            GlowManager.setGlow(level, pos, (float)glowStrength);

            simulateBlock(level, pos);

            operations++;
        }
    }

    /**
     * Simulates heat conduction from one block
     * into its neighbors.
     */
    private void simulateBlock(
            ServerLevel level,
            BlockPos pos
    ) {
        if (!level.hasChunkAt(pos)) {
            return;
        }

        if (level.getBlockState(pos).isAir()) {
            return;
        }

        simulateNeighbor(level, pos, Direction.EAST);
        simulateNeighbor(level, pos, Direction.WEST);
        simulateNeighbor(level, pos, Direction.UP);
        simulateNeighbor(level, pos, Direction.DOWN);
        simulateNeighbor(level, pos, Direction.SOUTH);
        simulateNeighbor(level, pos, Direction.NORTH);
    }

    /**
     * Calculates heat transfer between two neighboring blocks.
     */
    private void simulateNeighbor(
            ServerLevel level,
            BlockPos firstPos,
            Direction direction
    ) {

        BlockPos secondPos = firstPos.relative(direction);

        /*
         * Don't force-load neighboring chunks.
         */
        if (!level.hasChunkAt(secondPos)) {
            return;
        }

        /*
         * Air currently has no conduction simulation.
         * We'll handle air/convection separately later.
         */
        if (level.getBlockState(secondPos).isAir()) {
            return;
        }

        double firstTemperature =
                ThermalWorld.getTemperature(
                        level,
                        firstPos
                );

        double secondTemperature =
                ThermalWorld.getTemperature(
                        level,
                        secondPos
                );

        double deltaTemperature =
                firstTemperature - secondTemperature;

        /*
         * Already effectively equal temperature.
         */
        if (Math.abs(deltaTemperature) < 0.000001) {
            return;
        }

        ThermalMaterial firstMaterial =
                ThermalWorld.getMaterial(
                        level,
                        firstPos
                );

        ThermalMaterial secondMaterial =
                ThermalWorld.getMaterial(
                        level,
                        secondPos
                );

        /*
         * For two materials touching each other,
         * a simple first approximation is to use the
         * harmonic mean of their conductivities.
         *
         * k_eff = 2*k1*k2 / (k1+k2)
         */
        double k1 =
                firstMaterial.conductivity();

        double k2 =
                secondMaterial.conductivity();

        double effectiveConductivity;

        if (k1 <= 0.0 || k2 <= 0.0) {
            return;
        }

        effectiveConductivity =
                (2.0 * k1 * k2)
                        / (k1 + k2);

        /*
         * Fourier's law:
         *
         * P = k × A / L × ΔT
         *
         * P is watts = joules/second.
         */
        double power =
                effectiveConductivity
                        * CONTACT_AREA
                        / BLOCK_DISTANCE
                        * deltaTemperature;

        /*
         * Convert watts into joules for this tick.
         */
        double energyTransfer =
                power * DELTA_TIME;

        /*
         * Ignore extremely small transfers.
         */
        if (Math.abs(energyTransfer)
                < MIN_ENERGY_TRANSFER) {

            return;
        }

        /*
         * Positive energyTransfer means firstPos
         * is hotter.
         *
         * Negative means secondPos is hotter.
         */
        if (energyTransfer > 0.0) {

            transferEnergy(
                    level,
                    firstPos,
                    secondPos,
                    energyTransfer
            );

        } else {

            transferEnergy(
                    level,
                    secondPos,
                    firstPos,
                    -energyTransfer
            );
        }
    }

    /**
     * Transfers energy from one block to another.
     */
    private void transferEnergy(
            ServerLevel level,
            BlockPos source,
            BlockPos destination,
            double joules
    ) {

        if (joules <= 0.0) {
            return;
        }

        double sourceEnergy =
                ThermalWorld.getEnergy(
                        level,
                        source
                );

        /*
         * Energy is stored relative to the block's
         * default temperature.
         *
         * A block with negative energy is colder than
         * its default temperature.
         *
         * Therefore we cannot simply prevent all
         * negative-energy blocks from losing energy.
         */
        double actualTransfer = joules;

        /*
         * For now, limit transfer so a block cannot
         * cross its equilibrium point by an enormous
         * amount in one step.
         *
         * This is a simple stability safeguard.
         */
        double maximumStableTransfer =
                Math.abs(sourceEnergy) * 0.25;

        /*
         * If source energy is zero, it may still be
         * hotter than its neighbor because the neighbor
         * can have negative energy.
         *
         * In that case, allow the calculated transfer.
         */
        if (sourceEnergy > 0.0) {

            actualTransfer =
                    Math.min(
                            actualTransfer,
                            maximumStableTransfer
                    );
        }

        /*
         * Don't create energy.
         */
        if (actualTransfer <= 0.0) {
            return;
        }

        ThermalWorld.addEnergy(
                level,
                source,
                -actualTransfer
        );

        ThermalWorld.addEnergy(
                level,
                destination,
                actualTransfer
        );

        /*
         * Both blocks changed temperature, so
         * simulate them again.
         */
        activate(source);
        activate(destination);
    }
}
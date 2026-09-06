package Mods.create_tempratech.ThermalSystem.Simulation;

import net.minecraft.core.BlockPos;

import java.util.HashSet;

/**
 * Keeps track of blocks that are currently waiting
 * to be processed by the thermal simulation.
 *
 * We use packed long BlockPos values rather than BlockPos
 * objects in the queue.
 */
public final class ThermalActiveSet {

    private final HashSet<Long> active = new HashSet<>();

    /**
     * Activates a block.
     *
     * @return true if the block was not already active
     */
    public boolean activate(BlockPos pos) {
        return active.add(pos.asLong());
    }

    /**
     * Activates a packed position.
     */
    public boolean activate(long packedPos) {
        return active.add(packedPos);
    }

    /**
     * Removes a block from the active set.
     */
    public boolean deactivate(long packedPos) {
        return active.remove(packedPos);
    }

    /**
     * Returns whether a block is already active.
     */
    public boolean contains(long packedPos) {
        return active.contains(packedPos);
    }

    /**
     * Number of active blocks.
     */
    public int size() {
        return active.size();
    }

    /**
     * Clears all active blocks.
     */
    public void clear() {
        active.clear();
    }
}
package Mods.create_tempratech.ThermalSystem;

import Mods.create_tempratech.Client.IntDoubleHashTable;
import net.minecraft.core.BlockPos;

import java.util.function.BiConsumer;

public final class ThermalChunkData {

    /*
     * Only blocks whose energy differs from their default
     * temperature are stored here.
     */
    private final IntDoubleHashTable energy;

    public ThermalChunkData() {
        this.energy = new IntDoubleHashTable(16);
    }

    /**
     * Gets the energy stored for a block.
     *
     * If the block has no entry, it has 0 J relative
     * to its default temperature.
     */
    public double getEnergy(BlockPos pos) {
        return energy.get(getIndex(pos));
    }

    /**
     * Sets the energy of a block.
     *
     * 0 J removes the block from the sparse table.
     */
    public void setEnergy(BlockPos pos, double joules) {

        int index = getIndex(pos);

        if (joules == 0.0) {
            energy.remove(index);
            return;
        }

        energy.put(index, joules);
    }

    /**
     * Adds energy to a block.
     */
    public void addEnergy(BlockPos pos, double joules) {

        int index = getIndex(pos);

        energy.add(index, joules);

        /*
         * If the energy returns to exactly zero,
         * remove it from the sparse table.
         */
        if (energy.get(index) == 0.0) {
            energy.remove(index);
        }
    }

    /**
     * Removes all stored thermal energy for a block.
     */
    public void clearEnergy(BlockPos pos) {
        energy.remove(getIndex(pos));
    }

    /**
     * Returns true if this block has a stored
     * thermal energy value.
     */
    public boolean hasEnergyData(BlockPos pos) {
        return energy.containsKey(getIndex(pos));
    }

    /**
     * Number of blocks currently stored.
     */
    public int getStoredBlockCount() {
        return energy.size();
    }

    /**
     * Iterates over every stored thermal block.
     */
    public void forEachEnergy(BiConsumer<Integer, Double> consumer) {
        energy.forEach(consumer);
    }

    /**
     * Converts a BlockPos into a compact chunk-local index.
     *
     * Layout:
     *
     * x = bits 0-3
     * z = bits 4-7
     * y = bits 8-31
     */
    public static int getIndex(BlockPos pos) {

        int x = pos.getX() & 15;
        int z = pos.getZ() & 15;
        int y = pos.getY();

        return (y << 8) | (z << 4) | x;
    }

    public static int getX(int index) {
        return index & 15;
    }

    public static int getZ(int index) {
        return (index >> 4) & 15;
    }

    public static int getY(int index) {
        return index >> 8;
    }
}
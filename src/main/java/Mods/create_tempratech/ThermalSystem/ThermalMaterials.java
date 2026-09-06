package Mods.create_tempratech.ThermalSystem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class ThermalMaterials {

    /*
     * Default Minecraft reference temperature.
     *
     * 20 °C = 293.15 K
     */
    public static final double DEFAULT_TEMPERATURE_K = 293.15;

    /*
     * Generic fallback material.
     */
    public static final ThermalMaterial DEFAULT =
            new ThermalMaterial(
                    DEFAULT_TEMPERATURE_K,
                    1000.0,
                    1000.0,
                    1.0
            );

    /*
     * Stone.
     */
    public static final ThermalMaterial STONE =
            new ThermalMaterial(
                    293.15,
                    840.0,
                    2700.0,
                    2.5
            );

    /*
     * Iron.
     */
    public static final ThermalMaterial IRON =
            new ThermalMaterial(
                    293.15,
                    449.0,
                    7874.0,
                    80.0
            );

    /*
     * Water.
     */
    public static final ThermalMaterial WATER =
            new ThermalMaterial(
                    288.15,
                    4186.0,
                    1000.0,
                    0.6
            );

    /*
     * Dirt.
     */
    public static final ThermalMaterial DIRT =
            new ThermalMaterial(
                    293.15,
                    800.0,
                    1500.0,
                    1.5
            );

    /*
     * Sand.
     */
    public static final ThermalMaterial SAND =
            new ThermalMaterial(
                    293.15,
                    830.0,
                    1600.0,
                    1.8
            );

    /*
     * Ice.
     */
    public static final ThermalMaterial ICE =
            new ThermalMaterial(
                    268.15,
                    2100.0,
                    917.0,
                    2.2
            );

    /*
     * Lava.
     *
     * This is only an initial approximation.
     * We can later make lava temperature depend on
     * its exact composition/state.
     */
    public static final ThermalMaterial LAVA =
            new ThermalMaterial(
                    1273.15,
                    1500.0,
                    2700.0,
                    2.0
            );

    private ThermalMaterials() {
    }

    /**
     * Returns the thermal material associated with a Minecraft block.
     */
    public static ThermalMaterial getMaterial(Block block) {

        if (block == Blocks.STONE) {
            return STONE;
        }

        if (block == Blocks.IRON_BLOCK) {
            return IRON;
        }

        if (block == Blocks.WATER) {
            return WATER;
        }

        if (block == Blocks.DIRT) {
            return DIRT;
        }

        if (block == Blocks.SAND) {
            return SAND;
        }

        if (block == Blocks.ICE) {
            return ICE;
        }

        if (block == Blocks.LAVA) {
            return LAVA;
        }

        return DEFAULT;
    }
}
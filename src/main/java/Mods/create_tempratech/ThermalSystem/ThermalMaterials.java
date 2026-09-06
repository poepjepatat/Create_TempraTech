package Mods.create_tempratech.ThermalSystem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public final class ThermalMaterials {

    /*
     * Default Minecraft reference temperature.
     *
     * 20 °C = 293.15 K
     */
    public static final double DEFAULT_TEMPERATURE_K = 293.15;

    public static final Map<Block, ThermalMaterial> PROPERTIES =
            new HashMap<>();

    /*
     * Generic fallback material.
     */
    public static final ThermalMaterial DEFAULT =
            new ThermalMaterial(
                    DEFAULT_TEMPERATURE_K,
                    1000.0,
                    1000.0,
                    1.0,
                    500.0
            );

    static {
        PROPERTIES.put(
                Blocks.STONE,
                new ThermalMaterial(
                        293.15,
                        840.0,
                        2700.0,
                        2.5,
                        525.0
                )
        );

        PROPERTIES.put(
                Blocks.IRON_BLOCK,
                new ThermalMaterial(
                        293.15,
                        449.0,
                        7874.0,
                        80.0,
                        525.0
                )
        );

        PROPERTIES.put(
                Blocks.WATER,
                new ThermalMaterial(
                        288.15,
                        4186.0,
                        1000.0,
                        0.6,
                        525.0
                )
        );

        PROPERTIES.put(
                Blocks.DIRT,
                new ThermalMaterial(
                        293.15,
                        800.0,
                        1500.0,
                        1.5,
                        525.0
                )
        );

        PROPERTIES.put(
                Blocks.SAND,
                new ThermalMaterial(
                        293.15,
                        830.0,
                        1600.0,
                        1.8,
                        1100.0
                )
        );

        PROPERTIES.put(
                Blocks.ICE,
                new ThermalMaterial(
                        268.15,
                        2100.0,
                        917.0,
                        2.2,
                        525.0
                )
        );

        PROPERTIES.put(
                Blocks.LAVA,
                new ThermalMaterial(
                        1273.15,
                        1500.0,
                        2700.0,
                        2.0,
                        0.0
                )
        );
    }

    private ThermalMaterials() {
    }

    /**
     * Returns the thermal material associated with a Minecraft block.
     * Unknown blocks use the generic default material.
     */
    public static ThermalMaterial getMaterial(Block block) {
        return PROPERTIES.getOrDefault(block, DEFAULT);
    }
}
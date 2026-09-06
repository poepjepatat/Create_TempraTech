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
    public static Map<Block,ThermalMaterial> properties = new HashMap<>();
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

    private ThermalMaterials() {
        properties.put(Blocks.STONE,new ThermalMaterial(
                293.15,
                840.0,
                2700.0,
                2.5
        ));
        properties.put(Blocks.IRON_BLOCK,new ThermalMaterial(
                293.15,
                449.0,
                7874.0,
                80.0
        ));
        properties.put(Blocks.WATER,new ThermalMaterial(
                288.15,
                4186.0,
                1000.0,
                0.6
        ));
        properties.put(Blocks.DIRT,new ThermalMaterial(
                293.15,
                800.0,
                1500.0,
                1.5
        ));
        properties.put(Blocks.SAND,new ThermalMaterial(
                293.15,
                830.0,
                1600.0,
                1.8
        ));
        properties.put(Blocks.ICE, new ThermalMaterial(
                268.15,
                2100.0,
                917.0,
                2.2
        ));
        properties.put(Blocks.LAVA,new ThermalMaterial(
                1273.15,
                1500.0,
                2700.0,
                2.0
        ));

    }

    /**
     * Returns the thermal material associated with a Minecraft block.
     */
    public static ThermalMaterial getMaterial(Block block) {

        if (properties.containsKey(block)){
            return DEFAULT;
        }

        return properties.get(block);
    }
}
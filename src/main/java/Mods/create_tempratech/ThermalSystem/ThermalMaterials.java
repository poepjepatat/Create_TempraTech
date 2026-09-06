package Mods.create_tempratech.ThermalSystem;

import Mods.create_tempratech.Create_tempratech;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public final class ThermalMaterials {

    public static final double DEFAULT_TEMPERATURE_K = 293.15;

    public static final Map<Block, ThermalMaterial> PROPERTIES =
            new HashMap<>();

    private static final Map<ResourceLocation, ThermalMaterial>
            MOLTEN_PROPERTIES = new HashMap<>();

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
                material(293.15, 840.0, 2700.0, 2.5, 525.0)
        );
        PROPERTIES.put(
                Blocks.IRON_BLOCK,
                material(293.15, 449.0, 7874.0, 80.0, 525.0)
        );
        PROPERTIES.put(
                Blocks.GOLD_BLOCK,
                material(293.15, 129.0, 19300.0, 318.0, 450.0)
        );
        PROPERTIES.put(
                Blocks.COPPER_BLOCK,
                material(293.15, 385.0, 8960.0, 401.0, 500.0)
        );
        PROPERTIES.put(
                Blocks.WATER,
                material(288.15, 4186.0, 1000.0, 0.6, 525.0)
        );
        PROPERTIES.put(
                Blocks.DIRT,
                material(293.15, 800.0, 1500.0, 1.5, 525.0)
        );
        PROPERTIES.put(
                Blocks.SAND,
                material(293.15, 830.0, 1600.0, 1.8, 1100.0)
        );
        PROPERTIES.put(
                Blocks.ICE,
                material(268.15, 2100.0, 917.0, 2.2, 525.0)
        );
        PROPERTIES.put(
                Blocks.BASALT,
                material(293.15, 840.0, 3000.0, 1.7, 525.0)
        );
        PROPERTIES.put(
                Blocks.LAVA,
                material(1273.15, 1500.0, 2700.0, 2.0, 500.0)
        );

        registerMolten("iron", 1811.0, 820.0, 7000.0, 30.0, 525.0);
        registerMolten("gold", 1337.0, 150.0, 17300.0, 100.0, 450.0);
        registerMolten("copper", 1358.0, 600.0, 8000.0, 90.0, 500.0);
        registerMolten("zinc", 693.0, 500.0, 6500.0, 60.0, 350.0);
        registerMolten("coal", 1273.0, 1200.0, 1200.0, 2.0, 450.0);
        registerMolten("diamond", 3823.0, 500.0, 3000.0, 120.0, 600.0);
        registerMolten("emerald", 1573.0, 700.0, 2500.0, 15.0, 500.0);
        registerMolten("lapis", 1273.0, 800.0, 2500.0, 8.0, 450.0);
        registerMolten("redstone", 973.0, 700.0, 3000.0, 12.0, 400.0);
        registerMolten("quartz", 1986.0, 730.0, 2200.0, 10.0, 550.0);
        registerMolten("netherite", 2773.0, 500.0, 10000.0, 45.0, 650.0);
    }

    private ThermalMaterials() {
    }

    public static ThermalMaterial getMaterial(Block block) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        ThermalMaterial molten = MOLTEN_PROPERTIES.get(id);

        if (molten != null) {
            return molten;
        }

        return PROPERTIES.getOrDefault(block, DEFAULT);
    }

    private static ThermalMaterial material(
            double defaultTemperatureK,
            double specificHeat,
            double density,
            double conductivity,
            double glowTemperatureC
    ) {
        return new ThermalMaterial(
                defaultTemperatureK,
                specificHeat,
                density,
                conductivity,
                glowTemperatureC
        );
    }

    private static void registerMolten(
            String name,
            double defaultTemperatureK,
            double specificHeat,
            double density,
            double conductivity,
            double glowTemperatureC
    ) {
        MOLTEN_PROPERTIES.put(
                ResourceLocation.fromNamespaceAndPath(
                        Create_tempratech.MODID,
                        "molten_" + name
                ),
                material(
                        defaultTemperatureK,
                        specificHeat,
                        density,
                        conductivity,
                        glowTemperatureC
                )
        );
    }
}

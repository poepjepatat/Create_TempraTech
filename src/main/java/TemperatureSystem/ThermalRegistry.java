package TemperatureSystem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class ThermalRegistry {

    private static final Map<Block, TemperatureManager.ThermalProperties> DATA = new HashMap<>();

    public static void register(
            Block block,
            float temperature,
            float conductivity,
            float heatCapacity
    ) {
        DATA.put(
                block,
                new TemperatureManager.ThermalProperties(
                        temperature,
                        conductivity,
                        heatCapacity
                )
        );
    }

    public static TemperatureManager.ThermalProperties get(BlockState state) {
        return DATA.getOrDefault(
                state.getBlock(),
                new TemperatureManager.ThermalProperties(20.0f, 0.1f, 1.0f)
        );
    }


}
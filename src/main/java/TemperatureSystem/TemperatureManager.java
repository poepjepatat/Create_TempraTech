package TemperatureSystem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TemperatureManager {

    public record ThermalProperties(
            float baseTemperature,
            float conductivity,
            float heatCapacity
    ) {



    }

    public static void processBlock(
            Level level,
            BlockPos pos
    ) {
        BlockState state = level.getBlockState(pos);

        ThermalProperties properties =
                ThermalRegistry.get(state);

        float conductivity = properties.conductivity();
        float capacity = properties.heatCapacity();
        float baseTemperature = properties.baseTemperature();

        // Temperature calculations go here
    }

}

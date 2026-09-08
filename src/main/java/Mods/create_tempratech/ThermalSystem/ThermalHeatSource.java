package Mods.create_tempratech.ThermalSystem;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.Set;

public record ThermalHeatSource(
        double powerWatts,
        double maxTemperatureK,
        Map<String, Set<String>> stateConditions
) {

    public ThermalHeatSource {
        stateConditions = stateConditions == null
                ? Map.of()
                : Map.copyOf(stateConditions);
    }

    public boolean matches(BlockState state) {
        for (Map.Entry<String, Set<String>> condition
                : stateConditions.entrySet()) {
            Property<?> property = findProperty(
                    state,
                    condition.getKey()
            );

            if (property == null) {
                return false;
            }

            String value = serializedValue(state, property);
            if (!condition.getValue().contains(value)) {
                return false;
            }
        }

        return true;
    }

    private static Property<?> findProperty(
            BlockState state,
            String name
    ) {
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals(name)) {
                return property;
            }
        }

        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String serializedValue(
            BlockState state,
            Property property
    ) {
        Comparable value = state.getValue(property);
        return property.getName(value);
    }
}

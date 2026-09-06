package Mods.create_tempratech.ThermalSystem.Simulation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Maintains one thermal simulator per ServerLevel.
 */
public final class ThermalSimulationManager {

    private static final Map<ServerLevel, ThermalSimulator> SIMULATORS =
            new WeakHashMap<>();

    private ThermalSimulationManager() {
    }

    public static ThermalSimulator get(ServerLevel level) {
        return SIMULATORS.computeIfAbsent(
                level,
                ignored -> new ThermalSimulator()
        );
    }

    public static void tick(ServerLevel level) {
        get(level).tick(level);
    }

    /**
     * Seeds conduction after a world change. The changed block and every
     * neighbor must be reconsidered because either side can be hotter.
     */
    public static void activateBlockAndNeighbors(
            ServerLevel level,
            BlockPos pos
    ) {
        ThermalSimulator simulator = get(level);
        simulator.activate(pos);

        for (Direction direction : Direction.values()) {
            simulator.activate(pos.relative(direction));
        }
    }
}

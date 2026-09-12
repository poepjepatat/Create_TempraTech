package Mods.create_tempratech.Regs.Blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class PipeConnections {

    private PipeConnections() {
    }

    public static boolean canConnect(BlockState state, Direction direction) {
        return state.getBlock() instanceof ReinforcedPipe
                || state.getBlock() instanceof ElectroMagneticPipe;
    }
}

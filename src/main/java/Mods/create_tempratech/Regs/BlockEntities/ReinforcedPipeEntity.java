package Mods.create_tempratech.Regs.BlockEntities;

import Mods.create_tempratech.Regs.modBlockEntities;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

public class ReinforcedPipeEntity extends FluidPipeBlockEntity {
    public ReinforcedPipeEntity(BlockPos pos, BlockState state) {
        super(modBlockEntities.REINFORCED_PIPE_ENTITY.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new FluidTransportBehaviour(this) {
            @Override
            public boolean canHaveFlowToward(BlockState state, Direction direction) {
                return FluidPipeBlock.isPipe(state)
                        && FluidPipeBlock.isOpenAt(state, direction);
            }
        });
    }
}

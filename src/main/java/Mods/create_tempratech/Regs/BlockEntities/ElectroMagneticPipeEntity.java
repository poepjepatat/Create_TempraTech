package Mods.create_tempratech.Regs.BlockEntities;

import Mods.create_tempratech.Regs.modBlockEntities;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

public class ElectroMagneticPipeEntity extends FluidPipeBlockEntity {



    public ElectroMagneticPipeEntity(BlockPos pos, BlockState state) {
        super(modBlockEntities.ELECTRO_MAGNETIC_PIPE.get(), pos, state);
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

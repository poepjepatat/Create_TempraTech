package Mods.create_tempratech.Regs.Blocks;

import Mods.create_tempratech.Regs.modBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReinforcedPipe extends FluidPipeBlock {
    public static final MapCodec<ReinforcedPipe> CODEC =
            simpleCodec(ReinforcedPipe::new);

    public ReinforcedPipe(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends PipeBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        return modBlockEntities.REINFORCED_PIPE_ENTITY.get().create(pos, state);
    }

    public static net.minecraft.world.level.block.state.properties.BooleanProperty
    property(Direction direction) {
        return PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
    }
}

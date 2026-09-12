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

public class ElectroMagneticPipe extends FluidPipeBlock {
    public static final MapCodec<ElectroMagneticPipe> CODEC =
            simpleCodec(ElectroMagneticPipe::new);

    public ElectroMagneticPipe(Properties properties) {
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
        return modBlockEntities.ELECTRO_MAGNETIC_PIPE.get().create(pos, state);
    }

    public static net.minecraft.world.level.block.state.properties.BooleanProperty
    property(Direction direction) {
        return PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
    }
}

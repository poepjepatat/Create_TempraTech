package Mods.create_tempratech.Regs.Blocks;

import Mods.create_tempratech.Regs.BlockEntities.ActiveLiquidVentEntity;
import Mods.create_tempratech.Regs.modBlockEntities;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ActiveLiquidVent extends HorizontalKineticBlock
        implements IBE<ActiveLiquidVentEntity> {

    public static final MapCodec<ActiveLiquidVent> CODEC =
            simpleCodec(ActiveLiquidVent::new);

    public ActiveLiquidVent(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends HorizontalKineticBlock> codec() {
        return CODEC;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(
            LevelReader world,
            BlockPos pos,
            BlockState state,
            Direction face
    ) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public Class<ActiveLiquidVentEntity> getBlockEntityClass() {
        return ActiveLiquidVentEntity.class;
    }

    @Override
    public BlockEntityType<? extends ActiveLiquidVentEntity> getBlockEntityType() {
        return modBlockEntities.ACTIVE_LIQUID_VENT_ENTITY.get();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
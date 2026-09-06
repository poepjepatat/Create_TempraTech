package Mods.create_tempratech.Regs.Blocks;

import Mods.create_tempratech.Regs.BlockEntities.HeatHarvesterEntity;
import Mods.create_tempratech.Regs.modBlockEntities;
import com.mojang.serialization.MapCodec;
import Mods.create_tempratech.Regs.modBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HeatHarvester extends BaseEntityBlock{
    public static final MapCodec<HeatHarvester> CODEC = simpleCodec(HeatHarvester::new);
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    private static final VoxelShape BASE = Block.box(2, 0, 2, 14, 2, 14);
    private static final VoxelShape CORE = Block.box(6, 2, 6, 10, 10, 10);
    private static final VoxelShape UP_SHAPE = Block.box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape DOWN_SHAPE = Block.box(6, 0, 6, 10, 2, 10);
    private static final VoxelShape NORTH_SHAPE = Block.box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SOUTH_SHAPE = Block.box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape EAST_SHAPE = Block.box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 6, 6, 6, 10, 10);

    public HeatHarvester(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(UP, false).setValue(DOWN, false)
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    private boolean canConnect(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(modBlocks.HEAT_HARVESTER) || state.is(modBlocks.HEAT_PIPE);
    }

    private BlockState updateConnections(BlockState state, LevelAccessor level, BlockPos pos) {
        return state.setValue(UP, canConnect(level, pos.above()))
                .setValue(DOWN, canConnect(level, pos.below()))
                .setValue(NORTH, canConnect(level, pos.north()))
                .setValue(SOUTH, canConnect(level, pos.south()))
                .setValue(EAST, canConnect(level, pos.east()))
                .setValue(WEST, canConnect(level, pos.west()));
    }

    private VoxelShape getShapeForState(BlockState state) {
        VoxelShape shape = Shapes.or(BASE, CORE);
        if (state.getValue(UP)) shape = Shapes.or(shape, UP_SHAPE);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, DOWN_SHAPE);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, NORTH_SHAPE);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SOUTH_SHAPE);
        if (state.getValue(EAST)) shape = Shapes.or(shape, EAST_SHAPE);
        if (state.getValue(WEST)) shape = Shapes.or(shape, WEST_SHAPE);
        return shape;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShapeForState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return updateConnections(state, level, pos);
    }





    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new HeatHarvesterEntity(blockPos, blockState);
    }
}

package Mods.create_tempratech.Regs.Blocks;

import Mods.create_tempratech.Regs.modBlockEntities;
import Mods.create_tempratech.Regs.modBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class HeatPipe extends BaseEntityBlock {

    // =========================================================
    // CONNECTION PROPERTIES
    // =========================================================

    public static final BooleanProperty UP =
            BooleanProperty.create("up");

    public static final BooleanProperty DOWN =
            BooleanProperty.create("down");

    public static final BooleanProperty NORTH =
            BooleanProperty.create("north");

    public static final BooleanProperty SOUTH =
            BooleanProperty.create("south");

    public static final BooleanProperty EAST =
            BooleanProperty.create("east");

    public static final BooleanProperty WEST =
            BooleanProperty.create("west");


    // =========================================================
    // HITBOX / VOXEL SHAPES
    // =========================================================

    // Center of the pipe
    private static final VoxelShape CORE =
            Block.box(6, 6, 6, 10, 10, 10);

    // Up
    private static final VoxelShape UP_SHAPE =
            Block.box(6, 10, 6, 10, 16, 10);

    // Down
    private static final VoxelShape DOWN_SHAPE =
            Block.box(6, 0, 6, 10, 6, 10);

    // North (-Z)
    private static final VoxelShape NORTH_SHAPE =
            Block.box(6, 6, 0, 10, 10, 6);

    // South (+Z)
    private static final VoxelShape SOUTH_SHAPE =
            Block.box(6, 6, 10, 10, 10, 16);

    // East (+X)
    private static final VoxelShape EAST_SHAPE =
            Block.box(10, 6, 6, 16, 10, 10);

    // West (-X)
    private static final VoxelShape WEST_SHAPE =
            Block.box(0, 6, 6, 6, 10, 10);


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public HeatPipe(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(UP, false)
                        .setValue(DOWN, false)
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST, false)
                        .setValue(WEST, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }


    // =========================================================
    // UPDATE CONNECTIONS
    // =========================================================

    private BlockState updateConnections(
            BlockState state,
            net.minecraft.world.level.LevelAccessor level,
            BlockPos pos
    ) {
        return state
                .setValue(UP, canConnect(level, pos.above(), Direction.UP))
                .setValue(DOWN, canConnect(level, pos.below(), Direction.DOWN))
                .setValue(NORTH, canConnect(level, pos.north(), Direction.NORTH))
                .setValue(SOUTH, canConnect(level, pos.south(), Direction.SOUTH))
                .setValue(EAST, canConnect(level, pos.east(), Direction.EAST))
                .setValue(WEST, canConnect(level, pos.west(), Direction.WEST));
    }


    // =========================================================
    // CHECK CONNECTION
    // =========================================================

    private boolean canConnect(
            net.minecraft.world.level.LevelAccessor level,
            BlockPos pos,
            net.minecraft.core.Direction direction
    ) {
        BlockState state = level.getBlockState(pos);

        if (state.is(modBlocks.HEAT_PIPE)) {
            return true;
        }

        if (state.is(modBlocks.HEAT_HARVESTER)) {
            return direction != net.minecraft.core.Direction.UP;
        }

        if(state.is(modBlocks.THERMOMETER)){
            return direction == Direction.UP;
        }

        return false;
    }


    // =========================================================
    // PIPE HITBOX
    // =========================================================

    private VoxelShape getPipeShape(BlockState state) {

        VoxelShape shape = CORE;

        if (state.getValue(UP)) {
            shape = Shapes.or(shape, UP_SHAPE);
        }

        if (state.getValue(DOWN)) {
            shape = Shapes.or(shape, DOWN_SHAPE);
        }

        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, NORTH_SHAPE);
        }

        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, SOUTH_SHAPE);
        }

        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, EAST_SHAPE);
        }

        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, WEST_SHAPE);
        }

        return shape;
    }


    // =========================================================
    // RETURN HITBOX TO MINECRAFT
    // =========================================================

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return getPipeShape(state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    // =========================================================
    // UPDATE WHEN A NEIGHBOR CHANGES
    // =========================================================

    @Override
    protected BlockState updateShape(
            BlockState state,
            net.minecraft.core.Direction direction,
            BlockState neighborState,
            net.minecraft.world.level.LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        return updateConnections(
                state,
                level,
                pos
        );
    }


    // =========================================================
    // REGISTER BLOCKSTATE PROPERTIES
    // =========================================================

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(
                UP,
                DOWN,
                NORTH,
                SOUTH,
                EAST,
                WEST
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return modBlockEntities.HEAT_PIPE_ENTITY.get().create(pos, state);
    }
}

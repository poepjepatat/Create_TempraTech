package Mods.create_tempratech.Regs.Blocks;

import Mods.create_tempratech.Regs.modBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeatHarvester extends Block {

    // =========================================================
    // BLOCKSTATE PROPERTIES
    // =========================================================

    public static final BooleanProperty UP =
            BooleanProperty.create("up");

    public static final BooleanProperty NORTH =
            BooleanProperty.create("north");

    public static final BooleanProperty SOUTH =
            BooleanProperty.create("south");

    public static final BooleanProperty EAST =
            BooleanProperty.create("east");

    public static final BooleanProperty WEST =
            BooleanProperty.create("west");

    public static final BooleanProperty CORE =
            BooleanProperty.create("core");


    // =========================================================
    // VOXEL SHAPES
    // =========================================================

    // Harvester base
    private static final VoxelShape HARVESTER =
            Block.box(2, 0, 2, 14, 2, 14);

    // Pipe core
    private static final VoxelShape CORE_SHAPE =
            Block.box(6, 1, 6, 10, 10, 10);

    // Up
    private static final VoxelShape UP_SHAPE =
            Block.box(6, 10, 6, 10, 16, 10);

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

    public HeatHarvester(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(UP, false)
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST, false)
                        .setValue(WEST, false)
                        .setValue(CORE, false)
        );
    }


    // =========================================================
    // UPDATE CONNECTIONS
    // =========================================================

    private BlockState updateConnections(
            BlockState state,
            LevelAccessor level,
            BlockPos pos
    ) {
        return state
                .setValue(
                        UP,
                        canConnect(level, pos.above())
                )
                .setValue(
                        NORTH,
                        canConnect(level, pos.north())
                )
                .setValue(
                        SOUTH,
                        canConnect(level, pos.south())
                )
                .setValue(
                        EAST,
                        canConnect(level, pos.east())
                )
                .setValue(
                        WEST,
                        canConnect(level, pos.west())
                );
    }


    // =========================================================
    // CHECK CONNECTION
    // =========================================================

    private boolean canConnect(
            LevelAccessor level,
            BlockPos pos
    ) {
        BlockState state = level.getBlockState(pos);

        return state.is(modBlocks.HEAT_HARVESTER)
                || state.is(modBlocks.HEAT_PIPE);
    }


    // =========================================================
    // PIPE VOXEL SHAPE
    // =========================================================

    private VoxelShape getPipeShape(BlockState state) {

        VoxelShape shape = CORE_SHAPE;

        if (state.getValue(UP)) {
            shape = Shapes.or(
                    shape,
                    UP_SHAPE
            );
        }

        if (state.getValue(NORTH)) {
            shape = Shapes.or(
                    shape,
                    NORTH_SHAPE
            );
        }

        if (state.getValue(SOUTH)) {
            shape = Shapes.or(
                    shape,
                    SOUTH_SHAPE
            );
        }

        if (state.getValue(EAST)) {
            shape = Shapes.or(
                    shape,
                    EAST_SHAPE
            );
        }

        if (state.getValue(WEST)) {
            shape = Shapes.or(
                    shape,
                    WEST_SHAPE
            );
        }

        return shape;
    }


    // =========================================================
    // BLOCK SHAPE
    // =========================================================

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.or(
                HARVESTER,
                getPipeShape(state)
        );
    }


    // =========================================================
    // UPDATE SHAPE WHEN NEIGHBORS CHANGE
    // =========================================================

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
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
    // BLOCKSTATE PROPERTIES
    // =========================================================

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(
                UP,
                NORTH,
                SOUTH,
                EAST,
                WEST,
                CORE
        );
    }
}
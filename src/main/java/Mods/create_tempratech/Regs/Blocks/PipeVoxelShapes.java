package Mods.create_tempratech.Regs.Blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PipeVoxelShapes {

    private PipeVoxelShapes() {
    }

    public static VoxelShape getShape(
            BlockState state,
            BooleanProperty north,
            BooleanProperty east,
            BooleanProperty south,
            BooleanProperty west,
            BooleanProperty up,
            BooleanProperty down
    ) {
        VoxelShape shape = Block.box(4, 4, 4, 12, 12, 12);
        shape = addArm(shape, state, north,
                4, 4, 0, 12, 12, 12);
        shape = addArm(shape, state, east,
                4, 4, 4, 16, 12, 12);
        shape = addArm(shape, state, south,
                4, 4, 4, 12, 12, 16);
        shape = addArm(shape, state, west,
                0, 4, 4, 12, 12, 12);
        shape = addArm(shape, state, up,
                4, 4, 4, 12, 16, 12);
        shape = addArm(shape, state, down,
                4, 0, 4, 12, 12, 12);
        return shape;
    }

    private static VoxelShape addArm(
            VoxelShape shape,
            BlockState state,
            BooleanProperty property,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ
    ) {
        if (!state.getValue(property)) {
            return shape;
        }

        return Shapes.or(shape, Block.box(
                minX, minY, minZ, maxX, maxY, maxZ
        ));
    }
}

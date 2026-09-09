package Mods.create_tempratech.Regs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Uses the vanilla flow algorithm, but divides a side-spread's available
 * fluid level between branches instead of copying it to every branch.
 */
public abstract class ConservativeFlowingFluid extends BaseFlowingFluid {

    protected ConservativeFlowingFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void spread(
            Level level,
            BlockPos pos,
            FluidState state
    ) {
        spreadConservatively(this, level, pos, state);
    }

    private static void spreadConservatively(
            ConservativeFlowingFluid fluid,
            Level level,
            BlockPos pos,
            FluidState state
    ) {
        if (state.isEmpty()) {
            return;
        }

        BlockState currentBlock = level.getBlockState(pos);
        BlockPos below = pos.below();
        BlockState belowBlock = level.getBlockState(below);
        FluidState belowState = level.getFluidState(below);
        FluidState downward = fluid.getNewLiquid(level, below, belowBlock);

        int remaining = state.getAmount();
        int moved = 0;

        if (canAccept(
                fluid,
                level,
                pos,
                currentBlock,
                Direction.DOWN,
                below,
                belowBlock,
                belowState,
                downward.getType()
        )) {
            moved = transfer(
                    fluid,
                    level,
                    pos,
                    below,
                    belowBlock,
                    Direction.DOWN,
                    remaining
            );
            remaining -= moved;
        }

        if (remaining <= 0) {
            emptyOrigin(level, pos);
            return;
        }

        /*
         * A finite source may flow sideways when it cannot move downward.
         * Every side receives a share of the volume still held by this block.
         */
        List<Map.Entry<Direction, FluidState>> branches = new ArrayList<>();
        for (Map.Entry<Direction, FluidState> entry :
                fluid.getSpread(level, pos, currentBlock).entrySet()) {
            Direction direction = entry.getKey();
            BlockPos target = pos.relative(direction);
            BlockState targetBlock = level.getBlockState(target);
            FluidState targetState = level.getFluidState(target);

            if (canAccept(
                    fluid,
                    level,
                    pos,
                    currentBlock,
                    direction,
                    target,
                    targetBlock,
                    targetState,
                    entry.getValue().getType()
            )) {
                branches.add(entry);
            }
        }

        if (branches.isEmpty()) {
            return;
        }

        int baseAmount = remaining / branches.size();
        int remainder = remaining % branches.size();

        for (int index = 0; index < branches.size(); index++) {
            int branchAmount = baseAmount + (index < remainder ? 1 : 0);
            if (branchAmount <= 0) {
                continue;
            }

            Map.Entry<Direction, FluidState> branch = branches.get(index);
            BlockPos target = pos.relative(branch.getKey());
            int transferred = transfer(
                    fluid,
                    level,
                    pos,
                    target,
                    level.getBlockState(target),
                    branch.getKey(),
                    branchAmount
            );
            remaining -= transferred;
        }

        if (remaining < state.getAmount()) {
            if (remaining <= 0) {
                emptyOrigin(level, pos);
            } else {
                level.setBlock(
                        pos,
                        fluid.getFlowing(remaining, false)
                                .createLegacyBlock(),
                        3
                );
                level.scheduleTick(
                        pos,
                        fluid,
                        fluid.getTickDelay(level)
                );
            }
        }
    }

    private static boolean canAccept(
            ConservativeFlowingFluid fluid,
            Level level,
            BlockPos source,
            BlockState sourceBlock,
            Direction direction,
            BlockPos target,
            BlockState targetBlock,
            FluidState targetState,
            net.minecraft.world.level.material.Fluid targetFluid
    ) {
        if (fluid.isSame(targetState.getType())) {
            return targetState.getAmount() < FluidState.AMOUNT_FULL;
        }

        return fluid.canSpreadTo(
                level,
                source,
                sourceBlock,
                direction,
                target,
                targetBlock,
                targetState,
                targetFluid
        );
    }

    private static int transfer(
            ConservativeFlowingFluid fluid,
            Level level,
            BlockPos source,
            BlockPos target,
            BlockState targetBlock,
            Direction direction,
            int amount
    ) {
        if (amount <= 0) {
            return 0;
        }

        FluidState targetState = level.getFluidState(target);
        int capacity = fluid.isSame(targetState.getType())
                ? FluidState.AMOUNT_FULL - targetState.getAmount()
                : amount;
        int transferred = Math.min(amount, Math.max(0, capacity));

        if (transferred <= 0) {
            return 0;
        }

        int targetAmount = fluid.isSame(targetState.getType())
                ? targetState.getAmount() + transferred
                : transferred;
        FluidState placed = fluid.getFlowing(
                targetAmount,
                direction == Direction.DOWN
        );

        if (fluid.isSame(targetState.getType())) {
            level.setBlock(target, placed.createLegacyBlock(), 3);
            level.scheduleTick(
                    target,
                    fluid,
                    fluid.getTickDelay(level)
            );
        } else {
            fluid.spreadTo(
                    level,
                    target,
                    targetBlock,
                    direction,
                    placed
            );
        }

        return transferred;
    }

    private static void emptyOrigin(Level level, BlockPos pos) {
        level.setBlock(
                pos,
                net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
                3
        );
    }

    public static final class Source extends ConservativeFlowingFluid {

        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static final class Flowing extends ConservativeFlowingFluid {

        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        protected void createFluidStateDefinition(
                net.minecraft.world.level.block.state.StateDefinition.Builder<
                        net.minecraft.world.level.material.Fluid,
                        FluidState
                        > builder
        ) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}

package Mods.create_tempratech.mixin;

import Mods.create_tempratech.Regs.ConservativeFlowingFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Runs finite-volume flow for vanilla water and TempraTech's conservative
 * fluids. A fluid level is treated as an eighth of a bucket, so every move
 * transfers existing volume instead of letting vanilla level decay create or
 * delete volume.
 */
@Mixin(FlowingFluid.class)
public abstract class FlowingFluidConservationMixin extends Fluid {

    @Shadow
    protected abstract Map<Direction, FluidState> getSpread(
            Level level,
            BlockPos pos,
            BlockState state
    );

    @Shadow
    protected abstract boolean canSpreadTo(
            net.minecraft.world.level.BlockGetter level,
            BlockPos source,
            BlockState sourceState,
            Direction direction,
            BlockPos target,
            BlockState targetState,
            FluidState targetFluidState,
            Fluid fluid
    );

    @Shadow
    protected abstract void spreadTo(
            LevelAccessor level,
            BlockPos target,
            BlockState targetState,
            Direction direction,
            FluidState fluidState
    );

    @Shadow
    public abstract Fluid getFlowing();

    @Shadow
    public abstract FluidState getFlowing(int amount, boolean falling);

    @Shadow
    public abstract FluidState getSource(boolean falling);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void createTempratech$conserveFluidVolume(
            Level level,
            BlockPos pos,
            FluidState state,
            CallbackInfo callback
    ) {
        if (!createTempratech$usesFiniteVolume(state)) {
            return;
        }

        createTempratech$spreadConservatively(level, pos, state);
        callback.cancel();
    }

    private boolean createTempratech$usesFiniteVolume(FluidState state) {
        Fluid type = state.getType();
        return type == Fluids.WATER
                || type == Fluids.FLOWING_WATER
                || (Object) this instanceof ConservativeFlowingFluid;
    }

    private void createTempratech$spreadConservatively(
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

        int initialAmount = state.getAmount();
        int remaining = initialAmount;

        if (createTempratech$canAccept(
                level,
                pos,
                currentBlock,
                Direction.DOWN,
                below,
                belowBlock,
                belowState
        )) {
            int moved = createTempratech$transfer(
                    level,
                    below,
                    belowBlock,
                    Direction.DOWN,
                    remaining,
                    remaining
            );
            remaining -= moved;
        }

        if (remaining <= 0) {
            createTempratech$emptyOrigin(level, pos);
            return;
        }

        List<Direction> branches = new ArrayList<>();
        for (Map.Entry<Direction, FluidState> entry :
                getSpread(level, pos, currentBlock).entrySet()) {
            Direction direction = entry.getKey();
            BlockPos target = pos.relative(direction);
            BlockState targetBlock = level.getBlockState(target);
            FluidState targetState = level.getFluidState(target);

            if (createTempratech$canAccept(
                    level,
                    pos,
                    currentBlock,
                    direction,
                    target,
                    targetBlock,
                    targetState
            )) {
                branches.add(direction);
            }
        }

        if (branches.isEmpty()) {
            createTempratech$normalizeFullBlock(level, pos, state);
            return;
        }

        int baseAmount = remaining / branches.size();
        int remainder = remaining % branches.size();

        for (int index = 0; index < branches.size(); index++) {
            int branchAmount = baseAmount + (index < remainder ? 1 : 0);
            if (branchAmount <= 0) {
                continue;
            }

            Direction direction = branches.get(index);
            BlockPos target = pos.relative(direction);
            int transferred = createTempratech$transfer(
                    level,
                    target,
                    level.getBlockState(target),
                    direction,
                    branchAmount,
                    remaining
            );
            remaining -= transferred;
        }

        if (remaining == initialAmount) {
            createTempratech$normalizeFullBlock(level, pos, state);
            return;
        }

        if (remaining <= 0) {
            createTempratech$emptyOrigin(level, pos);
            return;
        }

        level.setBlock(
                pos,
                createTempratech$stateForAmount(remaining, false)
                        .createLegacyBlock(),
                3
        );
        createTempratech$scheduleIfCanSpread(level, pos);
    }

    private void createTempratech$scheduleIfCanSpread(
            Level level,
            BlockPos pos
    ) {
        FluidState state = level.getFluidState(pos);
        if (state.isEmpty() || !isSame(state.getType())) {
            return;
        }

        BlockState currentBlock = level.getBlockState(pos);
        BlockPos below = pos.below();
        BlockState belowBlock = level.getBlockState(below);
        if (createTempratech$canAccept(
                level,
                pos,
                currentBlock,
                Direction.DOWN,
                below,
                belowBlock,
                level.getFluidState(below)
        )) {
            level.scheduleTick(pos, state.getType(), getTickDelay(level));
            return;
        }

        for (Map.Entry<Direction, FluidState> entry :
                getSpread(level, pos, currentBlock).entrySet()) {
            Direction direction = entry.getKey();
            BlockPos target = pos.relative(direction);
            BlockState targetBlock = level.getBlockState(target);
            if (createTempratech$canAccept(
                    level,
                    pos,
                    currentBlock,
                    direction,
                    target,
                    targetBlock,
                    level.getFluidState(target)
            )) {
                level.scheduleTick(pos, state.getType(), getTickDelay(level));
                return;
            }
        }
    }

    private boolean createTempratech$canAccept(
            Level level,
            BlockPos source,
            BlockState sourceBlock,
            Direction direction,
            BlockPos target,
            BlockState targetBlock,
            FluidState targetState
    ) {
        FluidState sourceState = level.getFluidState(source);
        int sourceAmount = sourceState.getAmount();

        if (createTempratech$isSameFluid(targetState)) {
            if (direction == Direction.DOWN) {
                return targetState.getAmount() < FluidState.AMOUNT_FULL;
            }

            // Only equalize a meaningful horizontal height difference. A
            // one-unit difference is considered settled, which prevents two
            // low cells from sending the same volume back and forth forever.
            return sourceAmount > targetState.getAmount() + 1;
        }

        // One remaining level is a settled puddle. It may still fall down,
        // but it should not crawl sideways into an empty neighbor forever.
        if (direction != Direction.DOWN && sourceAmount <= 1) {
            return false;
        }

        return canSpreadTo(
                level,
                source,
                sourceBlock,
                direction,
                target,
                targetBlock,
                targetState,
                getFlowing()
        );
    }

    private int createTempratech$transfer(
            Level level,
            BlockPos target,
            BlockState targetBlock,
            Direction direction,
            int requestedAmount,
            int sourceAmount
    ) {
        if (requestedAmount <= 0 || sourceAmount <= 0) {
            return 0;
        }

        FluidState targetState = level.getFluidState(target);
        boolean sameFluid = createTempratech$isSameFluid(targetState);
        int targetAmount = sameFluid ? targetState.getAmount() : 0;
        int capacity = FluidState.AMOUNT_FULL - targetAmount;
        int transferable = Math.min(requestedAmount, Math.max(0, capacity));

        if (direction != Direction.DOWN) {
            // Move only enough to approach equilibrium. Integer division
            // deliberately leaves a one-unit height difference at rest.
            int equalizingAmount = Math.max(0, (sourceAmount - targetAmount) / 2);
            transferable = Math.min(transferable, equalizingAmount);
        }

        int transferred = Math.min(transferable, sourceAmount);
        if (transferred <= 0) {
            return 0;
        }

        int combinedAmount = targetAmount + transferred;
        FluidState placed = createTempratech$stateForAmount(
                combinedAmount,
                direction == Direction.DOWN
        );

        if (sameFluid) {
            level.setBlock(target, placed.createLegacyBlock(), 3);
        } else {
            spreadTo(level, target, targetBlock, direction, placed);
        }

        createTempratech$scheduleIfCanSpread(level, target);
        return transferred;
    }

    private FluidState createTempratech$stateForAmount(
            int amount,
            boolean falling
    ) {
        if (amount >= FluidState.AMOUNT_FULL && !falling) {
            return getSource(false);
        }
        return getFlowing(amount, falling);
    }

    private boolean createTempratech$isSameFluid(FluidState state) {
        return !state.isEmpty() && isSame(state.getType());
    }

    private void createTempratech$normalizeFullBlock(
            Level level,
            BlockPos pos,
            FluidState state
    ) {
        if (state.getAmount() != FluidState.AMOUNT_FULL || state.isSource()) {
            return;
        }

        FluidState source = getSource(false);
        level.setBlock(pos, source.createLegacyBlock(), 3);
    }

    private static void createTempratech$emptyOrigin(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }
}

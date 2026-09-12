package Mods.create_tempratech.mixin;

import Mods.create_tempratech.ThermalSystem.Simulation.ThermalSimulationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelThermalActivationMixin {

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void createTempratech$rejectNullBlockState(
            BlockPos pos,
            BlockState state,
            int flags,
            int recursionLeft,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (state == null) {
            callback.setReturnValue(false);
        }
    }

    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("RETURN")
    )
    private void createTempratech$activateThermalSimulation(
            BlockPos pos,
            BlockState state,
            int flags,
            int recursionLeft,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!callback.getReturnValueZ()) {
            return;
        }

        if ((Object) this instanceof ServerLevel serverLevel) {
            ThermalSimulationManager.activateBlockAndNeighbors(serverLevel, pos);
        }
    }
}

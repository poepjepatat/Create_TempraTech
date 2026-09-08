package Mods.create_tempratech.mixin;

import Mods.create_tempratech.ThermalSystem.ThermalMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockThermalLightMixin implements IBlockExtension {

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        return ThermalMaterials.canGlow(state);
    }

    @Override
    public int getLightEmission(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        int fixedEmission = state.getLightEmission();

        if (!hasDynamicLightEmission(state)) {
            return fixedEmission;
        }

        AuxiliaryLightManager lightManager =
                ((IBlockGetterExtension) level).getAuxLightManager(pos);
        int thermalEmission = lightManager == null
                ? 0
                : lightManager.getLightAt(pos);

        return Math.max(fixedEmission, thermalEmission);
    }
}

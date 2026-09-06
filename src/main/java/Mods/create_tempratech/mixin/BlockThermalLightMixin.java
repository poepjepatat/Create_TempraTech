package Mods.create_tempratech.mixin;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.ThermalSystem.ThermalMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockThermalLightMixin implements IBlockExtension {

    @Override
    public boolean hasDynamicLightEmission(BlockState state) {
        if (state.is(Tags.Blocks.ORES)
                || ThermalMaterials.PROPERTIES.containsKey(state.getBlock())) {
            return true;
        }

        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return Create_tempratech.MODID.equals(id.getNamespace())
                && id.getPath().startsWith("molten_");
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

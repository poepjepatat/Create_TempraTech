package Mods.create_tempratech.Regs.Liquids;

import Mods.create_tempratech.Regs.modFluidTypes;
import Mods.create_tempratech.Regs.modFluids;
import Mods.create_tempratech.Regs.modItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidType;

public class MoltenGold extends FlowingFluid{
    @Override
    public FlowingFluid getFlowing() {
        return modFluids.FLOWING_MOLTEN_GOLD.get();
    }

    @Override
    public FlowingFluid getSource() {
        return modFluids.MOLTEN_GOLD.get();
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {

    }

    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return 0;
    }

    @Override
    protected int getDropOff(LevelReader levelReader) {
        return 0;
    }

    @Override
    public Item getBucket() {
        return modItems.MOLTEN_GOLD_BUCKET.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState fluidState) {
        return net.minecraft.world.level.block.Blocks.LAVA.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return fluidState.getType() == getSource();
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return isSource(fluidState)
                ? 8
                : fluidState.getValue(FlowingFluid.LEVEL);
    }

    @Override
    public FluidType getFluidType() {
        return modFluidTypes.MOLTEN_GOLD_TYPE.get();
    }
}

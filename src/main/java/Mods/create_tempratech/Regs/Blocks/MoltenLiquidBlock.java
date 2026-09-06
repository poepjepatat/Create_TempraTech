package Mods.create_tempratech.Regs.Blocks;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;

public final class MoltenLiquidBlock extends LiquidBlock {

    public MoltenLiquidBlock(
            FlowingFluid fluid,
            BlockBehaviour.Properties properties
    ) {
        super(fluid, properties);
    }
}

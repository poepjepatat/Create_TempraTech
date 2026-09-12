package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Blocks.ActiveLiquidVent;
import Mods.create_tempratech.Regs.Blocks.ElectroMagneticPipe;
import Mods.create_tempratech.Regs.Blocks.ReinforcedPipe;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class modBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Create_tempratech.MODID);

    public static final DeferredBlock<ActiveLiquidVent> ACTIVE_VENT
            = BLOCKS.register("active_vent",
            () -> new ActiveLiquidVent(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<ReinforcedPipe> REINFORCED_PIPE
            = BLOCKS.register("reinforced_pipe",
            () -> new ReinforcedPipe(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<ElectroMagneticPipe> ELECTRO_MAGNETIC_PIPE
            = BLOCKS.register("electromagnetic_pipe",
            () -> new ElectroMagneticPipe(BlockBehaviour.Properties.of()));
}

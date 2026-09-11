package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Blocks.ActiveVent;
import Mods.create_tempratech.Regs.Blocks.HeatPipe;
import Mods.create_tempratech.Regs.Blocks.Thermometer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class modBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Create_tempratech.MODID);

    public static final DeferredBlock<HeatPipe> HEAT_PIPE
            = BLOCKS.register("heat_pipe",
            () -> new HeatPipe(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<Thermometer> THERMOMETER
            = BLOCKS.register("thermometer",
            () -> new Thermometer(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<ActiveVent> ACTIVE_VENT
            = BLOCKS.register("active_vent",
            () -> new ActiveVent(BlockBehaviour.Properties.of()));
}

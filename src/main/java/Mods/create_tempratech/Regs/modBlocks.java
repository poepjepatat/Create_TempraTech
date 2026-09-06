package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Blocks.HeatHarvester;
import Mods.create_tempratech.Regs.Blocks.HeatPipe;
import Mods.create_tempratech.Regs.Blocks.Thermometer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class modBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Create_tempratech.MODID);

    public static final DeferredBlock<HeatHarvester> HEAT_HARVESTER
            = BLOCKS.register("heat_harvester",
            () -> new HeatHarvester(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<HeatPipe> HEAT_PIPE
            = BLOCKS.register("heat_pipe",
            () -> new HeatPipe(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<Thermometer> THERMOMETER
            = BLOCKS.register("thermometer",
            () -> new Thermometer(BlockBehaviour.Properties.of()));

    public static final DeferredBlock<Block> MOLTEN_IRON
            = BLOCKS.register("molten_iron",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3.0F)
                            .lightLevel(state -> 15)
                            .noOcclusion()
            ));

    public static final DeferredBlock<Block> MOLTEN_GOLD
            = BLOCKS.register("molten_gold",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3.0F)
                            .lightLevel(state -> 15)
                            .noOcclusion()
            ));
}

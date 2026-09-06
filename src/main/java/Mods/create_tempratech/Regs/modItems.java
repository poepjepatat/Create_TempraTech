package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class modItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Create_tempratech.MODID);

    public static final DeferredItem<Item> TITANIUM_INGOT
            = ITEMS.register("titanium_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<BlockItem> HEAT_HARVESTER
            = ITEMS.registerSimpleBlockItem("heat_harvester",
            modBlocks.HEAT_HARVESTER);

    public static final DeferredItem<BlockItem> HEAT_PIPE
            = ITEMS.registerSimpleBlockItem("heat_pipe",
            modBlocks.HEAT_PIPE);

    public static final DeferredItem<BlockItem> THERMOMETER
            = ITEMS.registerSimpleBlockItem("thermometer",
            modBlocks.THERMOMETER);
}

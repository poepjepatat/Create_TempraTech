package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Items.ThermalGogglesItem;
import Mods.create_tempratech.Regs.Items.VisualThermalGogglesItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class modItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Create_tempratech.MODID);

    public static final DeferredItem<Item> TITANIUM_INGOT
            = ITEMS.register("titanium_ingot",
            () -> new Item(new Item.Properties()));



    public static final DeferredItem<BlockItem> HEAT_PIPE
            = ITEMS.registerSimpleBlockItem("heat_pipe",
            modBlocks.HEAT_PIPE);

    public static final DeferredItem<BlockItem> THERMOMETER
            = ITEMS.registerSimpleBlockItem("thermometer",
            modBlocks.THERMOMETER);

    public static final DeferredItem<ThermalGogglesItem> THERMAL_GOGGLES
            = ITEMS.register("thermal_goggles",
            () -> new ThermalGogglesItem(new Item.Properties()));

    public static final DeferredItem<VisualThermalGogglesItem> VISUAL_THERMAL_GOGGLES
            = ITEMS.register("visual_thermal_goggles",
            () -> new VisualThermalGogglesItem(new Item.Properties()));

    public static final DeferredItem<BucketItem> MOLTEN_IRON_BUCKET
            = ITEMS.register("molten_iron_bucket",
            () -> new BucketItem(modFluids.MOLTEN_IRON.get(),
                    new Item.Properties()));

    public static final DeferredItem<BucketItem> MOLTEN_GOLD_BUCKET
            = ITEMS.register("molten_gold_bucket",
            () -> new BucketItem(modFluids.MOLTEN_GOLD.get(),
                    new Item.Properties()));
    public static final DeferredItem<BucketItem> MOLTEN_COAL_BUCKET
            = ITEMS.register("molten_coal_bucket",
            () -> new BucketItem(modFluids.MOLTEN_COAL.get(),
                    new Item.Properties()));

    public static final DeferredItem<BucketItem> MOLTEN_COPPER_BUCKET
            = ITEMS.register("molten_copper_bucket",
            () -> new BucketItem(modFluids.MOLTEN_COPPER.get(),
                    new Item.Properties()));

    public static final DeferredItem<BucketItem> MOLTEN_DIAMOND_BUCKET
            = ITEMS.register("molten_diamond_bucket",
            () -> new BucketItem(modFluids.MOLTEN_DIAMOND.get(),
                    new Item.Properties()));

    public static final DeferredItem<BucketItem> MOLTEN_NETHERITE_BUCKET
            = ITEMS.register("molten_netherite_bucket",
            () -> new BucketItem(modFluids.MOLTEN_NETHERITE.get(),
                    new Item.Properties()));

    public static final DeferredItem<BucketItem> MOLTEN_ZINC_BUCKET
            = ITEMS.register("molten_zinc_bucket",
            () -> new BucketItem(modFluids.MOLTEN_ZINC.get(),
                    new Item.Properties()));

}

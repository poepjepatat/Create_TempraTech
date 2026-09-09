package Mods.create_tempratech;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Create_tempratech.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

    private static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    private static final ModConfigSpec.IntValue THERMAL_OPERATIONS_PER_TICK =
            BUILDER.comment("Maximum thermal simulation operations per server tick. Lower this on slower computers.")
                    .defineInRange("thermalOperationsPerTick", 2500, 250, 10000);
    private static final ModConfigSpec.IntValue VISUAL_SCAN_INTERVAL_TICKS =
            BUILDER.comment("Ticks between visual thermal goggle scans.")
                    .defineInRange("visualScanIntervalTicks", 20, 5, 100);
    private static final ModConfigSpec.IntValue VISUAL_SCAN_HORIZONTAL_RADIUS =
            BUILDER.comment("Horizontal block radius for visual thermal goggle scans.")
                    .defineInRange("visualScanHorizontalRadius", 12, 4, 32);
    private static final ModConfigSpec.IntValue VISUAL_SCAN_VERTICAL_RADIUS =
            BUILDER.comment("Vertical block radius for visual thermal goggle scans.")
                    .defineInRange("visualScanVerticalRadius", 8, 3, 20);
    private static final ModConfigSpec.IntValue MAX_VISUAL_SCAN_ENTRIES =
            BUILDER.comment("Maximum thermal blocks sent to a player in one visual scan.")
                    .defineInRange("maxVisualScanEntries", 2048, 256, 8192);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;
    public static int thermalOperationsPerTick = 2500;
    public static int visualScanIntervalTicks = 20;
    public static int visualScanHorizontalRadius = 12;
    public static int visualScanVerticalRadius = 8;
    public static int maxVisualScanEntries = 2048;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumber = MAGIC_NUMBER.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream().map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName))).collect(Collectors.toSet());
        thermalOperationsPerTick = THERMAL_OPERATIONS_PER_TICK.get();
        visualScanIntervalTicks = VISUAL_SCAN_INTERVAL_TICKS.get();
        visualScanHorizontalRadius = VISUAL_SCAN_HORIZONTAL_RADIUS.get();
        visualScanVerticalRadius = VISUAL_SCAN_VERTICAL_RADIUS.get();
        maxVisualScanEntries = MAX_VISUAL_SCAN_ENTRIES.get();
    }
}

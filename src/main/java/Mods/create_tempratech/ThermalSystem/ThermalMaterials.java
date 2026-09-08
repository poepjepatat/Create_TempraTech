package Mods.create_tempratech.ThermalSystem;

import Mods.create_tempratech.Create_tempratech;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ThermalMaterials {

    public static final double DEFAULT_TEMPERATURE_K = 293.15;

    public static final ThermalMaterial AIR =
            new ThermalMaterial(
                    DEFAULT_TEMPERATURE_K,
                    1005.0,
                    1.225,
                    0.026,
                    Double.POSITIVE_INFINITY
            );

    public static final ThermalMaterial DEFAULT =
            new ThermalMaterial(
                    DEFAULT_TEMPERATURE_K,
                    1000.0,
                    1000.0,
                    1.0,
                    500.0
            );

    private static volatile ThermalMaterial airMaterial = AIR;
    private static volatile ThermalMaterial defaultMaterial = DEFAULT;
    private static volatile Map<Block, ThermalBlockProperties>
            resolvedProperties = Map.of();

    private ThermalMaterials() {
    }

    public static ThermalMaterial getMaterial(BlockState state) {
        if (state.isAir()) {
            return airMaterial;
        }

        return getMaterial(state.getBlock());
    }

    public static ThermalMaterial getMaterial(Block block) {
        if (block == Blocks.AIR
                || block == Blocks.CAVE_AIR
                || block == Blocks.VOID_AIR) {
            return airMaterial;
        }

        ThermalBlockProperties properties =
                resolvedProperties.get(block);

        return properties == null
                ? defaultMaterial
                : properties.material();
    }

    public static boolean canGlow(BlockState state) {
        return !state.isAir()
                && Double.isFinite(
                        getMaterial(state).glowTemperature()
                );
    }

    public static ThermalHeatSource getActiveHeatSource(
            BlockState state
    ) {
        if (state.isAir()) {
            return null;
        }

        ThermalBlockProperties properties =
                resolvedProperties.get(state.getBlock());

        if (properties == null) {
            return null;
        }

        for (ThermalHeatSource heatSource
                : properties.heatSources()) {
            if (heatSource.matches(state)) {
                return heatSource;
            }
        }

        return null;
    }

    public static synchronized void reload(
            Map<ResourceLocation, JsonElement> resources
    ) {
        List<Map.Entry<ResourceLocation, JsonElement>> files =
                new ArrayList<>(resources.entrySet());

        files.sort(Comparator.comparing(
                entry -> entry.getKey().toString()
        ));

        ThermalMaterial loadedDefault = DEFAULT;
        ThermalMaterial loadedAir = AIR;
        int defaultPriority = Integer.MIN_VALUE;
        int airPriority = Integer.MIN_VALUE;

        List<ThermalRule> rules = new ArrayList<>();
        int order = 0;

        for (Map.Entry<ResourceLocation, JsonElement> file
                : files) {
            try {
                JsonObject root =
                        requireObject(
                                file.getValue(),
                                file.getKey().toString()
                        );
                int filePriority =
                        getInt(root, "priority", 0);

                if (root.has("default_material")
                        && filePriority >= defaultPriority) {
                    loadedDefault = parseMaterial(
                            root.getAsJsonObject(
                                    "default_material"
                            ),
                            file.getKey()
                                    + " default_material"
                    );
                    defaultPriority = filePriority;
                }

                if (root.has("air_material")
                        && filePriority >= airPriority) {
                    loadedAir = parseMaterial(
                            root.getAsJsonObject(
                                    "air_material"
                            ),
                            file.getKey()
                                    + " air_material"
                    );
                    airPriority = filePriority;
                }

                if (!root.has("rules")) {
                    continue;
                }

                JsonArray ruleArray =
                        root.getAsJsonArray("rules");

                for (JsonElement ruleElement : ruleArray) {
                    ThermalRule rule = parseRule(
                            requireObject(
                                    ruleElement,
                                    file.getKey()
                                            + " rule "
                                            + order
                            ),
                            filePriority,
                            order
                    );
                    rules.add(rule);
                    order++;
                }
            } catch (RuntimeException exception) {
                Create_tempratech.LOGGER.error(
                        "Failed to load thermal properties from {}",
                        file.getKey(),
                        exception
                );
            }
        }

        rules.sort(
                Comparator.comparingInt(
                                ThermalRule::priority
                        )
                        .thenComparingInt(
                                ThermalRule::order
                        )
        );

        Map<Block, ThermalBlockProperties> loaded =
                new HashMap<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            ThermalMaterial material = loadedDefault;
            List<ThermalHeatSource> heatSources =
                    List.of();

            for (ThermalRule rule : rules) {
                if (!rule.matches(block)) {
                    continue;
                }

                if (rule.material() != null) {
                    material = rule.material();
                }

                if (rule.heatSources() != null) {
                    heatSources = rule.heatSources();
                }
            }

            loaded.put(
                    block,
                    new ThermalBlockProperties(
                            material,
                            List.copyOf(heatSources)
                    )
            );
        }

        defaultMaterial = loadedDefault;
        airMaterial = loadedAir;
        resolvedProperties = Map.copyOf(loaded);

        long sourceBlockCount = loaded.values().stream()
                .filter(properties ->
                        !properties.heatSources().isEmpty())
                .count();

        Create_tempratech.LOGGER.info(
                "Loaded thermal JSON properties for {} blocks; {} blocks can generate heat",
                loaded.size(),
                sourceBlockCount
        );
    }

    private static ThermalRule parseRule(
            JsonObject object,
            int filePriority,
            int order
    ) {
        if (!object.has("match")) {
            throw new IllegalArgumentException(
                    "Thermal rule is missing match selector"
            );
        }

        JsonObject match =
                object.getAsJsonObject("match");

        Set<ResourceLocation> blocks =
                new HashSet<>();
        for (String blockId :
                readStrings(match.get("blocks"))) {
            blocks.add(ResourceLocation.parse(blockId));
        }

        Set<String> namespaces =
                new HashSet<>(
                        readStrings(
                                match.get("namespaces")
                        )
                );

        List<TagKey<Block>> tags =
                new ArrayList<>();
        for (String tagId :
                readStrings(match.get("tags"))) {
            tags.add(
                    TagKey.create(
                            Registries.BLOCK,
                            ResourceLocation.parse(tagId)
                    )
            );
        }

        if (blocks.isEmpty()
                && namespaces.isEmpty()
                && tags.isEmpty()) {
            throw new IllegalArgumentException(
                    "Thermal rule match selector is empty"
            );
        }

        ThermalMaterial material =
                object.has("material")
                        ? parseMaterial(
                                object.getAsJsonObject(
                                        "material"
                                ),
                                "thermal rule material"
                        )
                        : null;

        List<ThermalHeatSource> heatSources =
                object.has("heat_sources")
                        ? parseHeatSources(
                                object.getAsJsonArray(
                                        "heat_sources"
                                )
                        )
                        : null;

        if (material == null && heatSources == null) {
            throw new IllegalArgumentException(
                    "Thermal rule has neither material nor heat_sources"
            );
        }

        return new ThermalRule(
                filePriority
                        + getInt(
                                object,
                                "priority",
                                0
                        ),
                order,
                Set.copyOf(blocks),
                Set.copyOf(namespaces),
                List.copyOf(tags),
                material,
                heatSources == null
                        ? null
                        : List.copyOf(heatSources)
        );
    }

    private static ThermalMaterial parseMaterial(
            JsonObject object,
            String context
    ) {
        double defaultTemperatureK =
                requireDouble(
                        object,
                        "default_temperature_k",
                        context
                );
        double specificHeat =
                requireDouble(
                        object,
                        "specific_heat",
                        context
                );
        double density =
                requireDouble(
                        object,
                        "density",
                        context
                );
        double conductivity =
                requireDouble(
                        object,
                        "conductivity",
                        context
                );

        double glowTemperatureC =
                Double.POSITIVE_INFINITY;

        if (object.has("glow_temperature_c")
                && !object.get(
                        "glow_temperature_c"
                ).isJsonNull()) {
            glowTemperatureC =
                    object.get(
                            "glow_temperature_c"
                    ).getAsDouble();
        }

        if (defaultTemperatureK <= 0.0) {
            throw new IllegalArgumentException(
                    context
                            + " default_temperature_k must be > 0"
            );
        }

        if (specificHeat <= 0.0) {
            throw new IllegalArgumentException(
                    context
                            + " specific_heat must be > 0"
            );
        }

        if (density <= 0.0) {
            throw new IllegalArgumentException(
                    context
                            + " density must be > 0"
            );
        }

        if (conductivity < 0.0) {
            throw new IllegalArgumentException(
                    context
                            + " conductivity must be >= 0"
            );
        }

        return new ThermalMaterial(
                defaultTemperatureK,
                specificHeat,
                density,
                conductivity,
                glowTemperatureC
        );
    }

    private static List<ThermalHeatSource>
    parseHeatSources(JsonArray array) {
        List<ThermalHeatSource> heatSources =
                new ArrayList<>();

        for (JsonElement element : array) {
            JsonObject object =
                    requireObject(
                            element,
                            "heat source"
                    );

            double powerWatts =
                    requireDouble(
                            object,
                            "power_watts",
                            "heat source"
                    );
            double maxTemperatureK =
                    requireDouble(
                            object,
                            "max_temperature_k",
                            "heat source"
                    );

            if (powerWatts <= 0.0) {
                throw new IllegalArgumentException(
                        "heat source power_watts must be > 0"
                );
            }

            if (maxTemperatureK <= 0.0) {
                throw new IllegalArgumentException(
                        "heat source max_temperature_k must be > 0"
                );
            }

            Map<String, Set<String>> conditions =
                    new LinkedHashMap<>();

            if (object.has("when")) {
                JsonObject when =
                        object.getAsJsonObject("when");

                for (Map.Entry<String, JsonElement> entry
                        : when.entrySet()) {
                    conditions.put(
                            entry.getKey(),
                            Set.copyOf(
                                    readStrings(
                                            entry.getValue()
                                    )
                            )
                    );
                }
            }

            heatSources.add(
                    new ThermalHeatSource(
                            powerWatts,
                            maxTemperatureK,
                            Map.copyOf(conditions)
                    )
            );
        }

        return heatSources;
    }

    private static JsonObject requireObject(
            JsonElement element,
            String context
    ) {
        if (element == null || !element.isJsonObject()) {
            throw new IllegalArgumentException(
                    context + " must be a JSON object"
            );
        }

        return element.getAsJsonObject();
    }

    private static double requireDouble(
            JsonObject object,
            String key,
            String context
    ) {
        if (!object.has(key)) {
            throw new IllegalArgumentException(
                    context + " is missing " + key
            );
        }

        return object.get(key).getAsDouble();
    }

    private static int getInt(
            JsonObject object,
            String key,
            int fallback
    ) {
        return object.has(key)
                ? object.get(key).getAsInt()
                : fallback;
    }

    private static List<String> readStrings(
            JsonElement element
    ) {
        if (element == null || element.isJsonNull()) {
            return List.of();
        }

        List<String> values = new ArrayList<>();

        if (element.isJsonArray()) {
            for (JsonElement value :
                    element.getAsJsonArray()) {
                values.add(value.getAsString());
            }
        } else {
            values.add(element.getAsString());
        }

        return values;
    }

    private record ThermalBlockProperties(
            ThermalMaterial material,
            List<ThermalHeatSource> heatSources
    ) {
    }

    private record ThermalRule(
            int priority,
            int order,
            Set<ResourceLocation> blocks,
            Set<String> namespaces,
            List<TagKey<Block>> tags,
            ThermalMaterial material,
            List<ThermalHeatSource> heatSources
    ) {

        private boolean matches(Block block) {
            ResourceLocation blockId =
                    BuiltInRegistries.BLOCK.getKey(block);

            if (blocks.contains(blockId)) {
                return true;
            }

            if (namespaces.contains(
                    blockId.getNamespace()
            )) {
                return true;
            }

            BlockState defaultState =
                    block.defaultBlockState();

            for (TagKey<Block> tag : tags) {
                if (defaultState.is(tag)) {
                    return true;
                }
            }

            return false;
        }
    }
}

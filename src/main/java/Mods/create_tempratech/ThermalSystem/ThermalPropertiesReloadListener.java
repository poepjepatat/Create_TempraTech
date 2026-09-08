package Mods.create_tempratech.ThermalSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import java.util.Map;

public final class ThermalPropertiesReloadListener
        extends SimpleJsonResourceReloadListener {

    private static final Gson GSON =
            new GsonBuilder().create();

    public ThermalPropertiesReloadListener() {
        super(GSON, "thermal_properties");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> resources,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        ThermalMaterials.reload(resources);
    }

    @SubscribeEvent
    public static void onAddReloadListeners(
            AddReloadListenerEvent event
    ) {
        event.addListener(new ThermalPropertiesReloadListener());
    }
}

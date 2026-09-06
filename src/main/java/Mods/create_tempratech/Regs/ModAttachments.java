package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.ThermalSystem.ThermalChunkData;
import com.simibubi.create.Create;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.ATTACHMENT_TYPES,
                    Create_tempratech.MODID
            );

    public static final Supplier<AttachmentType<ThermalChunkData>> THERMAL_DATA =
            ATTACHMENT_TYPES.register(
                    "thermal_data",
                    () -> AttachmentType.builder(ThermalChunkData::new)
                            // Serialization will be added below.
                            .build()
            );

    private ModAttachments() {
    }

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
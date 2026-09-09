package Mods.create_tempratech.Network;

import Mods.create_tempratech.Client.Glowing.GlowManager;
import Mods.create_tempratech.Client.Glowing.VisualThermalManager;
import Mods.create_tempratech.Create_tempratech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ThermalVisualPayload(
        List<Entry> blocks
) implements CustomPacketPayload {

    public static final Type<ThermalVisualPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Create_tempratech.MODID,
                    "thermal_visual"
            ));

    public static final StreamCodec<ByteBuf, ThermalVisualPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(
                            java.util.ArrayList::new,
                            Entry.STREAM_CODEC
                    ),
                    ThermalVisualPayload::blocks,
                    ThermalVisualPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            ThermalVisualPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            Map<BlockPos, GlowManager.GlowData> snapshot = new HashMap<>();

            for (Entry entry : payload.blocks()) {
                BlockPos pos = BlockPos.of(entry.packedPos());
                snapshot.put(pos, colorFor(entry.temperatureC()));
            }

            VisualThermalManager.setSnapshot(level, snapshot);
        });
    }

    private static GlowManager.GlowData colorFor(float temperatureC) {
        float heat = clamp((temperatureC + 40.0F) / 500.0F);
        float red;
        float green;
        float blue;

        if (heat < 0.25F) {
            float local = heat * 4.0F;
            red = 0.05F;
            green = local * 0.7F;
            blue = 1.0F;
        } else if (heat < 0.5F) {
            float local = (heat - 0.25F) * 4.0F;
            red = 0.05F;
            green = 0.7F + local * 0.3F;
            blue = 1.0F - local;
        } else if (heat < 0.75F) {
            float local = (heat - 0.5F) * 4.0F;
            red = local;
            green = 1.0F;
            blue = 0.0F;
        } else {
            float local = (heat - 0.75F) * 4.0F;
            red = 1.0F;
            green = 1.0F - local * 0.85F;
            blue = 0.0F;
        }

        return new GlowManager.GlowData(
                0.3F + heat * 0.45F,
                red,
                green,
                blue
        );
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    public record Entry(long packedPos, float temperatureC) {

        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_LONG,
                        Entry::packedPos,
                        ByteBufCodecs.FLOAT,
                        Entry::temperatureC,
                        Entry::new
                );
    }
}

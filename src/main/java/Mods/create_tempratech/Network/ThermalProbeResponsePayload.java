package Mods.create_tempratech.Network;

import Mods.create_tempratech.Client.ThermalGoggles.ThermalProbeCache;
import Mods.create_tempratech.Create_tempratech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ThermalProbeResponsePayload(
        long packedPos,
        double temperatureC
) implements CustomPacketPayload {

    public static final Type<ThermalProbeResponsePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Create_tempratech.MODID,
                    "thermal_probe_response"
            ));

    public static final StreamCodec<ByteBuf, ThermalProbeResponsePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    ThermalProbeResponsePayload::packedPos,
                    ByteBufCodecs.DOUBLE,
                    ThermalProbeResponsePayload::temperatureC,
                    ThermalProbeResponsePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            ThermalProbeResponsePayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> ThermalProbeCache.accept(
                BlockPos.of(payload.packedPos()),
                payload.temperatureC()
        ));
    }
}

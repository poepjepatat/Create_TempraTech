package Mods.create_tempratech.Network;

import Mods.create_tempratech.Client.Glowing.GlowManager;
import Mods.create_tempratech.Create_tempratech;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ThermalGlowPayload(
        long packedPos,
        float strength,
        float red,
        float green,
        float blue
) implements CustomPacketPayload {

    public static final Type<ThermalGlowPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Create_tempratech.MODID,
                    "thermal_glow"
            ));

    public static final StreamCodec<ByteBuf, ThermalGlowPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    ThermalGlowPayload::packedPos,
                    ByteBufCodecs.FLOAT,
                    ThermalGlowPayload::strength,
                    ByteBufCodecs.FLOAT,
                    ThermalGlowPayload::red,
                    ByteBufCodecs.FLOAT,
                    ThermalGlowPayload::green,
                    ByteBufCodecs.FLOAT,
                    ThermalGlowPayload::blue,
                    ThermalGlowPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            ThermalGlowPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            BlockPos pos = BlockPos.of(payload.packedPos());

            if (payload.strength() <= 0.0F) {
                GlowManager.removeGlow(level, pos);
                return;
            }

            GlowManager.setGlow(
                    level,
                    pos,
                    payload.strength(),
                    payload.red(),
                    payload.green(),
                    payload.blue()
            );
        });
    }
}

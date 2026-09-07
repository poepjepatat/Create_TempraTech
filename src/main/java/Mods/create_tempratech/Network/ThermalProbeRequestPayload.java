package Mods.create_tempratech.Network;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Items.ThermalGogglesItem;
import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ThermalProbeRequestPayload(
        long packedPos
) implements CustomPacketPayload {

    public static final Type<ThermalProbeRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    Create_tempratech.MODID,
                    "thermal_probe_request"
            ));

    public static final StreamCodec<ByteBuf, ThermalProbeRequestPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    ThermalProbeRequestPayload::packedPos,
                    ThermalProbeRequestPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(
            ThermalProbeRequestPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (!(player.getItemBySlot(EquipmentSlot.HEAD).getItem()
                    instanceof ThermalGogglesItem)) {
                return;
            }

            ServerLevel level = player.serverLevel();
            BlockPos pos = BlockPos.of(payload.packedPos());

            if (!level.hasChunkAt(pos) || level.getBlockState(pos).isAir()) {
                return;
            }

            if (player.distanceToSqr(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5
            ) > 64.0) {
                return;
            }

            PacketDistributor.sendToPlayer(
                    player,
                    new ThermalProbeResponsePayload(
                            payload.packedPos(),
                            ThermalWorld.getTemperatureCelsius(level, pos)
                    )
            );
        });
    }
}

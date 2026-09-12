package Mods.create_tempratech.Client.Renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection.Flow;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class PipeFluidRenderer<T extends FluidPipeBlockEntity>
        extends SafeBlockEntityRenderer<T> {

    protected PipeFluidRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(
            T pipe,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        FluidTransportBehaviour transport =
                BlockEntityBehaviour.get(pipe.getLevel(), pipe.getBlockPos(),
                        FluidTransportBehaviour.TYPE);
        if (transport == null) {
            return;
        }

        for (Direction side : Iterate.directions) {
            Flow flow = transport.getFlow(side);
            if (flow == null) {
                continue;
            }

            FluidStack fluid = flow.fluid;
            LerpedFloat progress = flow.progress;
            if (fluid.isEmpty() || progress == null) {
                continue;
            }

            float value = Math.min(progress.getValue(partialTicks), 1.0F - 1.0E-6F);
            FluidRenderer.renderFluidStream(
                    fluid, side, 3 / 16f, value, flow.inbound,
                    buffer, poseStack, light
            );
        }
    }
}

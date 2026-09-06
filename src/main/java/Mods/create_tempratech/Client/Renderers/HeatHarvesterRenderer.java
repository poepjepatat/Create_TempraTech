package Mods.create_tempratech.Client.Renderers;

import Mods.create_tempratech.Regs.BlockEntities.HeatHarvesterEntity;
import Mods.create_tempratech.Regs.BlockEntities.HeatPipeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import static Mods.create_tempratech.Create_tempratech.LOGGER;

public class HeatHarvesterRenderer implements BlockEntityRenderer<HeatHarvesterEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public HeatHarvesterRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(
            HeatHarvesterEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        BlockState state = blockEntity.getBlockState();

        poseStack.pushPose();
        LOGGER.info("render test");
        blockRenderer.renderSingleBlock(
                state,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay,
                ModelData.EMPTY,
                null
        );
        poseStack.popPose();
    }
}
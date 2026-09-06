package Mods.create_tempratech.Client.Glowing;

import Mods.create_tempratech.Create_tempratech;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(
        modid = Create_tempratech.MODID,
        value = Dist.CLIENT
)
public final class GlowRenderer {

    private static final double MAX_RENDER_DISTANCE_SQR = 64.0 * 64.0;

    private GlowRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage()
                != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = new PoseStack();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource.BufferSource buffers =
                minecraft.renderBuffers().bufferSource();
        List<BlockPos> stalePositions = new ArrayList<>();

        GlowManager.forEach(level, (pos, data) -> {
            if (!level.hasChunkAt(pos)
                    || level.getBlockState(pos).isAir()) {
                stalePositions.add(pos);
                return;
            }

            double dx = pos.getX() + 0.5 - camera.x;
            double dy = pos.getY() + 0.5 - camera.y;
            double dz = pos.getZ() + 0.5 - camera.z;

            if (dx * dx + dy * dy + dz * dz
                    > MAX_RENDER_DISTANCE_SQR) {
                return;
            }

            float alpha = Mth.clamp(
                    0.08F + data.strength() * 0.30F,
                    0.08F,
                    0.40F
            );

            DebugRenderer.renderFilledBox(
                    poseStack,
                    buffers,
                    new AABB(pos).inflate(0.003),
                    data.red(),
                    data.green(),
                    data.blue(),
                    alpha
            );
        });

        buffers.endBatch(RenderType.debugFilledBox());
        stalePositions.forEach(pos -> GlowManager.removeGlow(level, pos));
    }
}

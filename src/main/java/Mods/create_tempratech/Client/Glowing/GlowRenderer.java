package Mods.create_tempratech.Client.Glowing;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Items.VisualThermalGogglesItem;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@EventBusSubscriber(
        modid = Create_tempratech.MODID,
        value = Dist.CLIENT
)
public final class GlowRenderer {

    private static final double MAX_GLOW_RENDER_DISTANCE_SQR = 64.0 * 64.0;
    private static final double MAX_VISUAL_RENDER_DISTANCE_SQR = 24.0 * 24.0;
    private static final ResourceLocation POST_CHAIN =
            ResourceLocation.fromNamespaceAndPath(
                    Create_tempratech.MODID,
                    "shaders/post/thermal_bloom.json"
            );
    private static final Map<BlockState, List<AABB>> SHAPE_CACHE =
            new HashMap<>();

    private static PostChain postChain;
    private static int postWidth = -1;
    private static int postHeight = -1;
    private static boolean postChainFailed;

    private GlowRenderer() {
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null) {
            return;
        }

        boolean visualGoggles = minecraft.player != null
                && minecraft.player.getItemBySlot(EquipmentSlot.HEAD)
                .getItem() instanceof VisualThermalGogglesItem;

        Vec3 camera = event.getCamera().getPosition();
        List<BlockPos> stalePositions = new ArrayList<>();
        List<VisibleGlow> visibleGlows = new ArrayList<>();

        if (!visualGoggles) {
            VisualThermalManager.clear(level);
        }

        BiConsumer<BlockPos, GlowManager.GlowData> addVisibleGlow =
                (pos, data) -> {
            if (!level.hasChunkAt(pos) || level.getBlockState(pos).isAir()) {
                if (!visualGoggles) {
                    stalePositions.add(pos);
                }
                return;
            }

            double dx = pos.getX() + 0.5 - camera.x;
            double dy = pos.getY() + 0.5 - camera.y;
            double dz = pos.getZ() + 0.5 - camera.z;

            double maxDistanceSqr = visualGoggles
                    ? MAX_VISUAL_RENDER_DISTANCE_SQR
                    : MAX_GLOW_RENDER_DISTANCE_SQR;

            if (dx * dx + dy * dy + dz * dz > maxDistanceSqr) {
                return;
            }

            visibleGlows.add(new VisibleGlow(pos.immutable(), data));
        };

        if (visualGoggles) {
            VisualThermalManager.forEach(level, addVisibleGlow);
        } else {
            GlowManager.forEach(level, addVisibleGlow);
        }

        if (!visualGoggles) {
            stalePositions.forEach(pos -> GlowManager.removeGlow(level, pos));
        }

        if (visibleGlows.isEmpty() || !ensurePostChain(minecraft)) {
            return;
        }

        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        RenderTarget maskTarget = postChain.getTempTarget("thermal_mask");

        if (maskTarget == null) {
            return;
        }

        maskTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        maskTarget.clear(Minecraft.ON_OSX);
        maskTarget.copyDepthFrom(mainTarget);
        maskTarget.bindWrite(true);

        // AFTER_LEVEL is dispatched without a live world PoseStack on NeoForge
        // 1.21.1. Rebuild the exact world transform from the matrices captured by
        // the event. Applying the model-view first and then camera translation
        // keeps the thermal mask locked to the same screen-space position as the
        // block geometry, including camera yaw/pitch and view bobbing.
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(event.getModelViewMatrix());
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource.BufferSource buffers =
                minecraft.renderBuffers().bufferSource();

        for (VisibleGlow glow : visibleGlows) {
            renderThermalMask(level, poseStack, buffers, glow);
        }

        buffers.endBatch(RenderType.debugFilledBox());
        maskTarget.unbindWrite();
        mainTarget.bindWrite(true);

        postChain.process(
                event.getPartialTick().getGameTimeDeltaPartialTick(false)
        );
        mainTarget.bindWrite(true);
    }

    private static void renderThermalMask(
            ClientLevel level,
            PoseStack poseStack,
            MultiBufferSource.BufferSource buffers,
            VisibleGlow glow
    ) {
        BlockPos pos = glow.pos();
        GlowManager.GlowData data = glow.data();
        BlockState state = level.getBlockState(pos);
        List<AABB> boxes = SHAPE_CACHE.computeIfAbsent(
                state,
                ignored -> thermalBoxes(level, pos, state)
        );

        float alpha = Math.max(0.08F, Math.min(1.0F, data.strength()));

        for (AABB localBox : boxes) {
            AABB worldBox = localBox
                    .move(pos.getX(), pos.getY(), pos.getZ());

            // This geometry is written only into the private thermal mask.
            DebugRenderer.renderFilledBox(
                    poseStack,
                    buffers,
                    worldBox,
                    data.red(),
                    data.green(),
                    data.blue(),
                    alpha
            );
        }
    }

    private static List<AABB> thermalBoxes(
            ClientLevel level,
            BlockPos pos,
            BlockState state
    ) {
        VoxelShape shape = state.getShape(level, pos);
        List<AABB> boxes = shape.toAabbs().stream()
                .toList();

        return boxes.isEmpty()
                ? List.of()
                : boxes;
    }

    private static boolean ensurePostChain(Minecraft minecraft) {
        if (postChainFailed) {
            return false;
        }

        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        if (postChain == null) {
            try {
                postChain = new PostChain(
                        minecraft.getTextureManager(),
                        minecraft.getResourceManager(),
                        mainTarget,
                        POST_CHAIN
                );
            } catch (IOException | RuntimeException exception) {
                postChainFailed = true;
                Create_tempratech.LOGGER.error(
                        "Failed to initialize TempraTech thermal post-processing",
                        exception
                );
                return false;
            }
        }

        if (postWidth != mainTarget.viewWidth
                || postHeight != mainTarget.viewHeight) {
            postWidth = mainTarget.viewWidth;
            postHeight = mainTarget.viewHeight;
            postChain.resize(postWidth, postHeight);
        }

        return true;
    }

    public static void invalidatePostChain() {
        if (postChain != null) {
            postChain.close();
            postChain = null;
        }

        postWidth = -1;
        postHeight = -1;
        postChainFailed = false;
    }

    private record VisibleGlow(
            BlockPos pos,
            GlowManager.GlowData data
    ) {
    }
}

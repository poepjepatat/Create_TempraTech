package Mods.create_tempratech.ThermalSystem.Simulation;

import Mods.create_tempratech.Regs.Blocks.ReinforcedPipe;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class PipeHeatHandler {

    private static final double MAX_PIPE_TEMPERATURE_C = 1050.0;
    private static final int MAX_PIPE_TEMPERATURE_K =
            (int) Math.ceil(MAX_PIPE_TEMPERATURE_C + 273.15);

    private static final Map<ServerLevel, Set<LevelChunk>> LOADED_CHUNKS =
            new IdentityHashMap<>();

    private PipeHeatHandler() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        LOADED_CHUNKS
                .computeIfAbsent(level, ignored ->
                        Collections.newSetFromMap(new IdentityHashMap<>()))
                .add(chunk);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        Set<LevelChunk> chunks = LOADED_CHUNKS.get(level);
        if (chunks == null) {
            return;
        }

        chunks.remove(chunk);
        if (chunks.isEmpty()) {
            LOADED_CHUNKS.remove(level);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (Map.Entry<ServerLevel, Set<LevelChunk>> entry : LOADED_CHUNKS.entrySet()) {
            breakOverheatedPipes(entry.getKey(), entry.getValue());
        }
    }

    private static void breakOverheatedPipes(
            ServerLevel level,
            Set<LevelChunk> chunks
    ) {
        Set<BlockEntity> overheatedPipes =
                Collections.newSetFromMap(new IdentityHashMap<>());

        for (LevelChunk chunk : chunks) {
            for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (isOverheatedNormalPipe(blockEntity)) {
                    overheatedPipes.add(blockEntity);
                }
            }
        }

        for (BlockEntity blockEntity : overheatedPipes) {
            if (!blockEntity.isRemoved()
                    && blockEntity.getLevel() == level
                    && blockEntity.getBlockState().getBlock() instanceof FluidPipeBlock
                    && !(blockEntity.getBlockState().getBlock() instanceof ReinforcedPipe)) {
                level.destroyBlock(blockEntity.getBlockPos(), true);
            }
        }
    }

    private static boolean isOverheatedNormalPipe(BlockEntity blockEntity) {
        if (!(blockEntity instanceof FluidPipeBlockEntity)
                || !(blockEntity.getBlockState().getBlock() instanceof FluidPipeBlock)
                || blockEntity.getBlockState().getBlock() instanceof ReinforcedPipe) {
            return false;
        }

        FluidTransportBehaviour transport =
                BlockEntityBehaviour.get(blockEntity.getLevel(), blockEntity.getBlockPos(),
                        FluidTransportBehaviour.TYPE);
        if (transport == null) {
            return false;
        }

        for (Direction direction : Direction.values()) {
            PipeConnection.Flow flow = transport.getFlow(direction);
            if (flow != null
                    && !flow.fluid.isEmpty()
                    && flow.fluid.getFluid().getFluidType().getTemperature(flow.fluid)
                    > MAX_PIPE_TEMPERATURE_K) {
                return true;
            }
        }

        return false;
    }
}

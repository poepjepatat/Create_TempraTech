package Mods.create_tempratech.Client.Renderers;

import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes;
import com.simibubi.create.content.fluids.FluidTransportBehaviour.AttachmentTypes.ComponentPartials;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CustomPipeAttachmentModel extends BakedModelWrapperWithData {
    private static final ModelProperty<PipeModelData> PIPE_PROPERTY =
            new ModelProperty<>();
    private static final Map<String, Map<ComponentPartials, Map<Direction, PartialModel>>>
            ATTACHMENTS = new java.util.HashMap<>();

    private final String modelNamespace;
    private final boolean ao;

    public static boolean initialize() {
        ATTACHMENTS.computeIfAbsent(
                "reinforced_pipe",
                ignored -> createAttachmentsFor("reinforced_pipe"));
        ATTACHMENTS.computeIfAbsent(
                "electromagnetic_pipe",
                ignored -> createAttachmentsFor("electromagnetic_pipe"));
        return true;
    }

    public static CustomPipeAttachmentModel withAO(
            BakedModel template,
            String modelNamespace
    ) {
        return new CustomPipeAttachmentModel(template, modelNamespace, true);
    }

    private CustomPipeAttachmentModel(
            BakedModel template,
            String modelNamespace,
            boolean ao
    ) {
        super(template);
        this.modelNamespace = modelNamespace;
        this.ao = ao;
    }

    @Override
    protected ModelData.Builder gatherModelData(
            ModelData.Builder builder,
            BlockAndTintGetter world,
            BlockPos pos,
            BlockState state,
            ModelData blockEntityData
    ) {
        PipeModelData data = new PipeModelData();
        FluidTransportBehaviour transport =
                BlockEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
        BracketedBlockEntityBehaviour bracket =
                BlockEntityBehaviour.get(
                        world, pos, BracketedBlockEntityBehaviour.TYPE);

        if (transport != null) {
            for (Direction direction : Iterate.directions) {
                data.putAttachment(
                        direction,
                        transport.getRenderedRimAttachment(
                                world, pos, state, direction));
            }
        }
        if (bracket != null) {
            data.putBracket(bracket.getBracket());
        }
        return builder.with(PIPE_PROPERTY, data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(
            BlockState state,
            RandomSource random,
            ModelData data
    ) {
        List<ChunkRenderTypeSet> renderTypes = new ArrayList<>();
        renderTypes.add(super.getRenderTypes(state, random, data));
        if (data.has(PIPE_PROPERTY)) {
            PipeModelData pipeData = data.get(PIPE_PROPERTY);
            for (Direction direction : Iterate.directions) {
                for (ComponentPartials partial :
                        pipeData.getAttachment(direction).partials) {
                    renderTypes.add(getPartial(partial, direction)
                            .get()
                            .getRenderTypes(state, random, data));
                }
            }
        }
        return ChunkRenderTypeSet.union(renderTypes);
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            Direction side,
            RandomSource random,
            ModelData data,
            RenderType renderType
    ) {
        List<BakedQuad> quads =
                new ArrayList<>(super.getQuads(
                        state, side, random, data, renderType));
        if (!data.has(PIPE_PROPERTY)) {
            return quads;
        }

        PipeModelData pipeData = data.get(PIPE_PROPERTY);
        if (pipeData.getBracket() != null) {
            quads.addAll(pipeData.getBracket().getQuads(
                    state, side, random, data, renderType));
        }
        for (Direction direction : Iterate.directions) {
            for (ComponentPartials partial :
                    pipeData.getAttachment(direction).partials) {
                quads.addAll(getPartial(partial, direction).get().getQuads(
                        state, side, random, data, renderType));
            }
        }
        return quads;
    }

    @Override
    public TriState useAmbientOcclusion(
            BlockState state,
            ModelData data,
            RenderType renderType
    ) {
        return ao ? TriState.TRUE : TriState.FALSE;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ao;
    }

    private PartialModel getPartial(
            ComponentPartials partial,
            Direction direction
    ) {
        return ATTACHMENTS
                .computeIfAbsent(modelNamespace, ignored -> createAttachments())
                .get(partial)
                .get(direction);
    }

    private Map<ComponentPartials, Map<Direction, PartialModel>>
    createAttachments() {
        return createAttachmentsFor(modelNamespace);
    }

    private static Map<ComponentPartials, Map<Direction, PartialModel>>
    createAttachmentsFor(String modelNamespace) {
        Map<ComponentPartials, Map<Direction, PartialModel>> result =
                new EnumMap<>(ComponentPartials.class);
        for (ComponentPartials partial : ComponentPartials.values()) {
            Map<Direction, PartialModel> directions =
                    new EnumMap<>(Direction.class);
            for (Direction direction : Iterate.directions) {
                directions.put(direction, PartialModel.of(
                        ResourceLocation.fromNamespaceAndPath(
                                "create_tempratech",
                                "block/" + modelNamespace + "_attachments/"
                                        + partial.name().toLowerCase() + "/"
                                        + direction.getSerializedName()
                )));
            }
            result.put(partial, directions);
        }
        return result;
    }

    private static class PipeModelData {
        private final AttachmentTypes[] attachments =
                new AttachmentTypes[6];
        private BakedModel bracket;

        private PipeModelData() {
            Arrays.fill(attachments, AttachmentTypes.NONE);
        }

        private void putAttachment(
                Direction direction,
                AttachmentTypes attachment
        ) {
            attachments[direction.get3DDataValue()] = attachment;
        }

        private AttachmentTypes getAttachment(Direction direction) {
            return attachments[direction.get3DDataValue()];
        }

        private void putBracket(BlockState state) {
            if (state != null) {
                bracket = Minecraft.getInstance()
                        .getBlockRenderer()
                        .getBlockModel(state);
            }
        }

        private BakedModel getBracket() {
            return bracket;
        }
    }
}

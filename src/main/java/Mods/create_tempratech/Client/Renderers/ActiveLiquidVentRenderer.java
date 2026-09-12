package Mods.create_tempratech.Client.Renderers;

import Mods.create_tempratech.Regs.BlockEntities.ActiveLiquidVentEntity;
import Mods.create_tempratech.Regs.TTPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class ActiveLiquidVentRenderer extends KineticBlockEntityRenderer<ActiveLiquidVentEntity> {

    public ActiveLiquidVentRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(ActiveLiquidVentEntity be, BlockState state) {
        return CachedBuffers.partial(TTPartialModels.ACTIVE_LIQUID_VENT_COG, state);
    }
}

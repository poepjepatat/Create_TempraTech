package Mods.create_tempratech.Client.Renderers;

import Mods.create_tempratech.Regs.BlockEntities.ReinforcedPipeEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ReinforcedPipeRenderer extends PipeFluidRenderer<ReinforcedPipeEntity> {
    public ReinforcedPipeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}

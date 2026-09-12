package Mods.create_tempratech.Client.Renderers;

import Mods.create_tempratech.Regs.BlockEntities.ElectroMagneticPipeEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class ElectroMagneticPipeRenderer
        extends PipeFluidRenderer<ElectroMagneticPipeEntity> {
    public ElectroMagneticPipeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
}

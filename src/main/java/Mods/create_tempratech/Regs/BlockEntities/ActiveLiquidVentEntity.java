package Mods.create_tempratech.Regs.BlockEntities;

import Mods.create_tempratech.Regs.modBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ActiveLiquidVentEntity extends KineticBlockEntity {
    public ActiveLiquidVentEntity(BlockPos pos, BlockState state) {
        super(modBlockEntities.ACTIVE_LIQUID_VENT_ENTITY.get(), pos, state);
    }
}

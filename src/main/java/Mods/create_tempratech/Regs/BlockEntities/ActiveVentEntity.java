package Mods.create_tempratech.Regs.BlockEntities;

import Mods.create_tempratech.Regs.modBlockEntities;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ActiveVentEntity extends KineticBlockEntity {
    public ActiveVentEntity(BlockPos pos, BlockState state) {
        super(modBlockEntities.ACTIVE_VENT_ENTITY.get(), pos, state);
    }
}

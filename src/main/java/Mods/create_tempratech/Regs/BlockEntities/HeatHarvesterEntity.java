package Mods.create_tempratech.Regs.BlockEntities;

import Mods.create_tempratech.Regs.modBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HeatHarvesterEntity extends BlockEntity {

    public HeatHarvesterEntity(BlockPos pos, BlockState state) {
        super(modBlockEntities.HEAT_HARVESTER_ENTITY.get(), pos, state);
    }

}

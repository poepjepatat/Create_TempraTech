package Mods.create_tempratech.Regs.BlockEntities;

import Mods.create_tempratech.Regs.modBlockEntities;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ThermometerEntity extends BlockEntity
    implements IHaveGoggleInformation {


    public ThermometerEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(modBlockEntities.THERMOMETER_ENTITY.get(), pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(
            List<Component> tooltip,
            boolean isPlayerSneaking
    ){
        return true;
    }
}

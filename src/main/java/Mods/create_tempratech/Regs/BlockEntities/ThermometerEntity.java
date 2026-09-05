package Mods.create_tempratech.Regs.BlockEntities;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ThermometerEntity extends BlockEntity
    implements IHaveGoggleInformation {


    public ThermometerEntity(BlockEntityType<?> blockentitytype, BlockPos pos, BlockState state) {
        super(blockentitytype, pos, state);
    }

    @Override
    public boolean addToGoggleTooltip(
            List<Component> tooltip,
            boolean isPlayerSneaking
    ){
        tooltip.add(Component.literal())
    }
}

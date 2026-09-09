package Mods.create_tempratech.Regs.Items;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.item.ArmorItem;

public class ThermalGogglesItem extends Item implements Equipable {

    public ThermalGogglesItem(Properties properties) {
        super(properties.stacksTo(1));
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
        GogglesItem.addIsWearingPredicate(
                player -> player.getItemBySlot(EquipmentSlot.HEAD).is(this)
        );
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        return swapWithEquipmentSlot(this, level, player, hand);
    }
}

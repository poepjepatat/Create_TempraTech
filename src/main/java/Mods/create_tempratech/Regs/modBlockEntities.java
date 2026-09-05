package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.BlockEntities.ThermometerEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class modBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Create_tempratech.MODID
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThermometerEntity>>
            THERMOMETER
            = BLOCK_ENTITIES.register("thermometer",
            () -> BlockEntityType.Builder.of(
                    ThermometerEntity::new,
                    modBlocks.THERMOMETER.get()
            ).build(null)
    );
}

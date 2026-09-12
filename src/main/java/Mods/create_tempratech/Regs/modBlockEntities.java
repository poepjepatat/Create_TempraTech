package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.BlockEntities.ActiveLiquidVentEntity;
import Mods.create_tempratech.Regs.BlockEntities.ElectroMagneticPipeEntity;
import Mods.create_tempratech.Regs.BlockEntities.ReinforcedPipeEntity;
import Mods.create_tempratech.Regs.Blocks.ElectroMagneticPipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class modBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Create_tempratech.MODID
            );



    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ElectroMagneticPipeEntity>>
            ELECTRO_MAGNETIC_PIPE
            = BLOCK_ENTITIES.register("electromagnetic_pipe",
            () -> BlockEntityType.Builder.of(
                    ElectroMagneticPipeEntity::new,
                    modBlocks.ELECTRO_MAGNETIC_PIPE.get()
            ).build(null));


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ActiveLiquidVentEntity>>
            ACTIVE_LIQUID_VENT_ENTITY
            = BLOCK_ENTITIES.register("active_liquid_vent",
            () -> BlockEntityType.Builder.of(
                    ActiveLiquidVentEntity::new,
                    modBlocks.ACTIVE_VENT.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ReinforcedPipeEntity>>
            REINFORCED_PIPE_ENTITY
            = BLOCK_ENTITIES.register("reinforced_pipe",
            () -> BlockEntityType.Builder.of(
                    ReinforcedPipeEntity::new,
                    modBlocks.REINFORCED_PIPE.get()
            ).build(null)
    );
}

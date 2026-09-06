package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Blocks.MoltenLiquidBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public final class modFluids {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.FLUID_TYPES,
                    Create_tempratech.MODID
            );

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(
                    Registries.FLUID,
                    Create_tempratech.MODID
            );

    public static final MoltenFluidDefinition IRON = register(
            "iron", 1811, 7000, 7000, 0xFFFF6A00
    );
    public static final MoltenFluidDefinition GOLD = register(
            "gold", 1337, 17300, 6500, 0xFFFFC400
    );
    public static final MoltenFluidDefinition COPPER = register(
            "copper", 1358, 8000, 6500, 0xFFFF7A3D
    );
    public static final MoltenFluidDefinition ZINC = register(
            "zinc", 693, 6500, 6000, 0xFF9FD8D8
    );
    public static final MoltenFluidDefinition COAL = register(
            "coal", 1273, 1200, 8000, 0xFF5A1E0B
    );
    public static final MoltenFluidDefinition DIAMOND = register(
            "diamond", 3823, 3000, 9000, 0xFF62F5E8
    );
    public static final MoltenFluidDefinition EMERALD = register(
            "emerald", 1573, 2500, 8000, 0xFF28D96B
    );
    public static final MoltenFluidDefinition LAPIS = register(
            "lapis", 1273, 2500, 8000, 0xFF315BD6
    );
    public static final MoltenFluidDefinition REDSTONE = register(
            "redstone", 973, 3000, 7000, 0xFFFF2418
    );
    public static final MoltenFluidDefinition QUARTZ = register(
            "quartz", 1986, 2200, 8500, 0xFFF4E7D3
    );
    public static final MoltenFluidDefinition NETHERITE = register(
            "netherite", 2773, 10000, 10000, 0xFF5A4248
    );

    private static final List<MoltenFluidDefinition> ALL = List.of(
            IRON,
            GOLD,
            COPPER,
            ZINC,
            COAL,
            DIAMOND,
            EMERALD,
            LAPIS,
            REDSTONE,
            QUARTZ,
            NETHERITE
    );

    private modFluids() {
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }

    public static List<MoltenFluidDefinition> all() {
        return ALL;
    }

    private static MoltenFluidDefinition register(
            String materialName,
            int temperatureK,
            int density,
            int viscosity,
            int tintColor
    ) {
        return new MoltenFluidDefinition(
                materialName,
                temperatureK,
                density,
                viscosity,
                tintColor
        );
    }

    public static final class MoltenFluidDefinition {
        private final String materialName;
        private final int tintColor;
        private final DeferredHolder<FluidType, FluidType> type;
        private final DeferredHolder<Fluid, BaseFlowingFluid.Source> source;
        private final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing;
        private final DeferredBlock<MoltenLiquidBlock> block;
        private final DeferredItem<BucketItem> bucket;

        private MoltenFluidDefinition(
                String materialName,
                int temperatureK,
                int density,
                int viscosity,
                int tintColor
        ) {
            this.materialName = materialName;
            this.tintColor = tintColor;

            String fluidName = "molten_" + materialName;

            this.type = FLUID_TYPES.register(
                    fluidName,
                    () -> new FluidType(
                            FluidType.Properties.create()
                                    .descriptionId(
                                            "fluid."
                                                    + Create_tempratech.MODID
                                                    + "."
                                                    + fluidName
                                    )
                                    .lightLevel(15)
                                    .temperature(temperatureK)
                                    .density(density)
                                    .viscosity(viscosity)
                    )
            );

            this.source = FLUIDS.register(
                    fluidName,
                    () -> new BaseFlowingFluid.Source(createProperties())
            );

            this.flowing = FLUIDS.register(
                    "flowing_" + fluidName,
                    () -> new BaseFlowingFluid.Flowing(createProperties())
            );

            this.block = modBlocks.BLOCKS.register(
                    fluidName,
                    () -> new MoltenLiquidBlock(
                            source.get(),
                            BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA)
                                    .lightLevel(state -> 15)
                    )
            );

            this.bucket = modItems.ITEMS.register(
                    fluidName + "_bucket",
                    () -> new BucketItem(
                            source.get(),
                            new Item.Properties()
                                    .craftRemainder(Items.BUCKET)
                                    .stacksTo(1)
                    )
            );
        }

        private BaseFlowingFluid.Properties createProperties() {
            return new BaseFlowingFluid.Properties(type, source, flowing)
                    .block(block)
                    .bucket(bucket)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(2)
                    .tickRate(20)
                    .explosionResistance(100.0F);
        }

        public String materialName() {
            return materialName;
        }

        public int tintColor() {
            return tintColor;
        }

        public DeferredHolder<FluidType, FluidType> type() {
            return type;
        }

        public DeferredHolder<Fluid, BaseFlowingFluid.Source> source() {
            return source;
        }

        public DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing() {
            return flowing;
        }

        public DeferredBlock<MoltenLiquidBlock> block() {
            return block;
        }

        public DeferredItem<BucketItem> bucket() {
            return bucket;
        }
    }
}

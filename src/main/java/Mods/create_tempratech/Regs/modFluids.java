package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Liquids.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
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

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Create_tempratech.MODID);

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_IRON =
            FLUIDS.register("molten_iron", MoltenIron::new);

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MOLTEN_IRON =
            FLUIDS.register("flowing_molten_iron", MoltenIron::new);

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_GOLD =
            FLUIDS.register("molten_gold", MoltenGold::new);

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MOLTEN_GOLD =
            FLUIDS.register("flowing_molten_gold", MoltenGold::new);

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_COPPER =
            FLUIDS.register("molten_copper", MoltenCopper::new);

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MOLTEN_COPPER =
            FLUIDS.register("flowing_molten_copper", MoltenCopper::new);

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_COAL =
            FLUIDS.register("molten_coal", MoltenCoal::new);

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MOLTEN_COAL =
            FLUIDS.register("flowing_molten_coal", MoltenCoal::new);

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_DIAMOND =
            FLUIDS.register("molten_diamond", MoltenDiamond::new);

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MOLTEN_DIAMOND =
            FLUIDS.register("flowing_molten_diamond", MoltenDiamond::new);

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_NETHERITE =
            FLUIDS.register("molten_netherite", MoltenNetherite::new);

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MOLTEN_NETHERRITE =
            FLUIDS.register("flowing_molten_netherite", MoltenNetherite::new);

    public static final DeferredHolder<Fluid, FlowingFluid> MOLTEN_ZINC =
            FLUIDS.register("molten_zinc", MoltenZinc::new);

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MOLTEN_ZINC =
            FLUIDS.register("flowing_molten_zinc", MoltenZinc::new);
}

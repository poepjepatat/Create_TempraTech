package Mods.create_tempratech.Regs;

import Mods.create_tempratech.Create_tempratech;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class modFluidTypes {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, Create_tempratech.MODID);

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_DIAMOND_TYPE =
            FLUID_TYPES.register("molten_diamond", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(5000)
                            .viscosity(10000)
                            .temperature(2000)));

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_IRON_TYPE =
            FLUID_TYPES.register("molten_iron", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(5000)
                            .viscosity(10000)
                            .temperature(2000)));

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_GOLD_TYPE =
            FLUID_TYPES.register("molten_gold", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(5000)
                            .viscosity(10000)
                            .temperature(2000)));

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_COAL_TYPE =
            FLUID_TYPES.register("molten_coal", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(5000)
                            .viscosity(10000)
                            .temperature(2000)));

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_COPPER_TYPE =
            FLUID_TYPES.register("molten_copper", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(5000)
                            .viscosity(10000)
                            .temperature(2000)));

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_ZINC_TYPE =
            FLUID_TYPES.register("molten_zinc", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(5000)
                            .viscosity(10000)
                            .temperature(2000)));

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_NETHERITE_TYPE =
            FLUID_TYPES.register("molten_netherite", () ->
                    new FluidType(FluidType.Properties.create()
                            .density(5000)
                            .viscosity(10000)
                            .temperature(2000)));
}

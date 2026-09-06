package Mods.create_tempratech.ThermalSystem.Simulation;

import Mods.create_tempratech.Regs.modFluids;
import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public final class ThermalTransformations {

    public static final double LAVA_BASALT_POINT_C = 700.0;

    private static final TagKey<Block> ZINC_ORES = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("c", "ores/zinc")
    );

    private static final List<MeltRule> MELT_RULES = List.of(
            rule(
                    Tags.Blocks.ORES_IRON,
                    "minecraft:iron_block",
                    modFluids.IRON,
                    1538.0,
                    1500.0
            ),
            rule(
                    Tags.Blocks.ORES_GOLD,
                    "minecraft:gold_block",
                    modFluids.GOLD,
                    1064.0,
                    1020.0
            ),
            rule(
                    Tags.Blocks.ORES_COPPER,
                    "minecraft:copper_block",
                    modFluids.COPPER,
                    1085.0,
                    1040.0
            ),
            rule(
                    ZINC_ORES,
                    "create:zinc_block",
                    modFluids.ZINC,
                    420.0,
                    390.0
            ),
            rule(
                    Tags.Blocks.ORES_COAL,
                    "minecraft:coal_block",
                    modFluids.COAL,
                    1000.0,
                    900.0
            ),
            rule(
                    Tags.Blocks.ORES_DIAMOND,
                    "minecraft:diamond_block",
                    modFluids.DIAMOND,
                    3550.0,
                    3450.0
            ),
            rule(
                    Tags.Blocks.ORES_EMERALD,
                    "minecraft:emerald_block",
                    modFluids.EMERALD,
                    1300.0,
                    1230.0
            ),
            rule(
                    Tags.Blocks.ORES_LAPIS,
                    "minecraft:lapis_block",
                    modFluids.LAPIS,
                    1000.0,
                    900.0
            ),
            rule(
                    Tags.Blocks.ORES_REDSTONE,
                    "minecraft:redstone_block",
                    modFluids.REDSTONE,
                    700.0,
                    650.0
            ),
            rule(
                    Tags.Blocks.ORES_QUARTZ,
                    "minecraft:quartz_block",
                    modFluids.QUARTZ,
                    1713.0,
                    1650.0
            ),
            rule(
                    Tags.Blocks.ORES_NETHERITE_SCRAP,
                    "minecraft:netherite_block",
                    modFluids.NETHERITE,
                    2500.0,
                    2400.0
            )
    );

    private ThermalTransformations() {
    }

    /**
     * Applies temperature-driven block transformations while preserving the
     * block's absolute temperature across the material change.
     */
    public static boolean tryTransform(
            ServerLevel level,
            BlockPos pos,
            double temperatureC
    ) {
        BlockState state = level.getBlockState(pos);

        for (MeltRule rule : MELT_RULES) {
            Block solidBlock = BuiltInRegistries.BLOCK.get(rule.solidBlockId());

            if ((state.is(rule.oreTag()) || state.is(solidBlock))
                    && temperatureC >= rule.meltingPointC()) {
                return replacePreservingTemperature(
                        level,
                        pos,
                        rule.molten().block().get().defaultBlockState(),
                        temperatureC
                );
            }

            if (isMolten(state.getFluidState(), rule.molten())
                    && temperatureC <= rule.solidifyPointC()) {
                return replacePreservingTemperature(
                        level,
                        pos,
                        solidBlock.defaultBlockState(),
                        temperatureC
                );
            }
        }

        if (state.is(Blocks.LAVA)
                && temperatureC <= LAVA_BASALT_POINT_C) {
            return replacePreservingTemperature(
                    level,
                    pos,
                    Blocks.BASALT.defaultBlockState(),
                    temperatureC
            );
        }

        return false;
    }

    private static boolean isMolten(
            FluidState fluidState,
            modFluids.MoltenFluidDefinition molten
    ) {
        return fluidState.is(molten.source().get())
                || fluidState.is(molten.flowing().get());
    }

    private static boolean replacePreservingTemperature(
            ServerLevel level,
            BlockPos pos,
            BlockState replacement,
            double temperatureC
    ) {
        if (!level.setBlockAndUpdate(pos, replacement)) {
            return false;
        }

        ThermalWorld.setTemperatureCelsius(level, pos, temperatureC);
        ThermalSimulationManager.activateBlockAndNeighbors(level, pos);
        return true;
    }

    private static MeltRule rule(
            TagKey<Block> oreTag,
            String solidBlockId,
            modFluids.MoltenFluidDefinition molten,
            double meltingPointC,
            double solidifyPointC
    ) {
        return new MeltRule(
                oreTag,
                ResourceLocation.parse(solidBlockId),
                molten,
                meltingPointC,
                solidifyPointC
        );
    }

    private record MeltRule(
            TagKey<Block> oreTag,
            ResourceLocation solidBlockId,
            modFluids.MoltenFluidDefinition molten,
            double meltingPointC,
            double solidifyPointC
    ) {
    }
}

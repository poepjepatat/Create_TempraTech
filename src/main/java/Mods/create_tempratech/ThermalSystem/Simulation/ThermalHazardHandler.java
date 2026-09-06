package Mods.create_tempratech.ThermalSystem.Simulation;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class ThermalHazardHandler {

    private static final int CHECK_INTERVAL_TICKS = 10;
    private static final int SEARCH_RADIUS = 2;
    private static final double NEARBY_DAMAGE_THRESHOLD_C = 200.0;
    private static final double CONTACT_DAMAGE_THRESHOLD_C = 120.0;

    public static final TagKey<Item> HEAT_PROTECTIVE_ARMOR = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(
                    Create_tempratech.MODID,
                    "heat_protective_armor"
            )
    );

    private ThermalHazardHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!(player.level() instanceof ServerLevel level)
                || player.getAbilities().invulnerable
                || player.tickCount % CHECK_INTERVAL_TICKS != 0) {
            return;
        }

        int protectivePieces = countProtectiveArmor(player);
        if (protectivePieces >= 4) {
            return;
        }

        BlockPos feet = player.blockPosition();
        double contactTemperature = Math.max(
                hottestAt(level, feet, feet.above()),
                temperatureAt(level, feet.below())
        );
        double nearbyTemperature = hottestNearby(level, feet);

        float damage = 0.0F;

        if (contactTemperature >= CONTACT_DAMAGE_THRESHOLD_C) {
            damage = 1.5F + (float) Math.min(
                    4.5,
                    (contactTemperature - CONTACT_DAMAGE_THRESHOLD_C) / 300.0
            );
        } else if (nearbyTemperature >= NEARBY_DAMAGE_THRESHOLD_C) {
            damage = 0.5F + (float) Math.min(
                    2.5,
                    (nearbyTemperature - NEARBY_DAMAGE_THRESHOLD_C) / 500.0
            );
        }

        if (damage <= 0.0F) {
            return;
        }

        float protectionMultiplier = 1.0F - protectivePieces * 0.25F;
        damage *= Math.max(0.0F, protectionMultiplier);

        if (damage >= 0.25F) {
            player.hurt(level.damageSources().hotFloor(), damage);
        }
    }

    private static int countProtectiveArmor(Player player) {
        int count = 0;

        for (ItemStack armor : player.getArmorSlots()) {
            if (!armor.isEmpty() && armor.is(HEAT_PROTECTIVE_ARMOR)) {
                count++;
            }
        }

        return count;
    }

    private static double hottestAt(
            ServerLevel level,
            BlockPos first,
            BlockPos second
    ) {
        return Math.max(
                temperatureAt(level, first),
                temperatureAt(level, second)
        );
    }

    private static double hottestNearby(ServerLevel level, BlockPos center) {
        double hottest = Double.NEGATIVE_INFINITY;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-SEARCH_RADIUS, -1, -SEARCH_RADIUS),
                center.offset(SEARCH_RADIUS, 2, SEARCH_RADIUS)
        )) {
            hottest = Math.max(hottest, temperatureAt(level, pos));
        }

        return hottest;
    }

    private static double temperatureAt(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos) || level.getBlockState(pos).isAir()) {
            return Double.NEGATIVE_INFINITY;
        }

        return ThermalWorld.getTemperatureCelsius(level, pos);
    }
}

package Mods.create_tempratech.Client.ThermalGoggles;

import Mods.create_tempratech.Create_tempratech;
import Mods.create_tempratech.Regs.Items.ThermalGogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@EventBusSubscriber(
        modid = Create_tempratech.MODID,
        value = Dist.CLIENT
)
public final class ThermalGoggleOverlay {

    private ThermalGoggleOverlay() {
    }

    @SubscribeEvent
    public static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.options.hideGui
                || minecraft.level == null
                || minecraft.player == null) {
            return;
        }

        if (!(minecraft.player.getItemBySlot(EquipmentSlot.HEAD).getItem()
                instanceof ThermalGogglesItem)) {
            ThermalProbeCache.clear();
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult hitResult)) {
            ThermalProbeCache.clear();
            return;
        }

        BlockPos pos = hitResult.getBlockPos();

        // Air has a simulated temperature, but thermal goggles deliberately
        // expose only the temperature of a physical targeted block.
        if (minecraft.level.getBlockState(pos).isAir()) {
            ThermalProbeCache.clear();
            return;
        }

        ThermalProbeClient.request(pos);
        OptionalDouble cached = ThermalProbeCache.get(pos);
        List<Component> tooltip = new ArrayList<>();

        new ThermalGoggleInformation(
                cached.isPresent(),
                cached.orElse(0.0)
        ).addToGoggleTooltip(
                tooltip,
                minecraft.player.isShiftKeyDown()
        );

        List<FormattedCharSequence> visual = tooltip.stream()
                .map(Component::getVisualOrderText)
                .toList();

        event.getGuiGraphics().renderTooltip(
                minecraft.font,
                visual,
                event.getGuiGraphics().guiWidth() / 2 + 16,
                event.getGuiGraphics().guiHeight() / 2 + 16
        );
    }
}

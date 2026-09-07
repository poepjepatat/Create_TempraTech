package Mods.create_tempratech.Client.ThermalGoggles;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public final class ThermalGoggleInformation implements IHaveGoggleInformation {

    private final boolean hasTemperature;
    private final double temperatureC;

    public ThermalGoggleInformation(boolean hasTemperature, double temperatureC) {
        this.hasTemperature = hasTemperature;
        this.temperatureC = temperatureC;
    }

    @Override
    public boolean addToGoggleTooltip(
            List<Component> tooltip,
            boolean isPlayerSneaking
    ) {
        tooltip.add(Component.translatable(
                "gui.create_tempratech.thermal_goggles"
        ).withStyle(ChatFormatting.GOLD));

        if (!hasTemperature) {
            tooltip.add(Component.translatable(
                    "gui.create_tempratech.thermal_goggles.scanning"
            ).withStyle(ChatFormatting.GRAY));
            return true;
        }

        String celsius = String.format(Locale.ROOT, "%.1f", temperatureC);
        String kelvin = String.format(Locale.ROOT, "%.1f", temperatureC + 273.15);

        tooltip.add(Component.translatable(
                "gui.create_tempratech.thermal_goggles.temperature",
                celsius
        ).withStyle(ChatFormatting.GRAY));

        if (isPlayerSneaking) {
            tooltip.add(Component.translatable(
                    "gui.create_tempratech.thermal_goggles.kelvin",
                    kelvin
            ).withStyle(ChatFormatting.DARK_GRAY));
        }

        return true;
    }
}

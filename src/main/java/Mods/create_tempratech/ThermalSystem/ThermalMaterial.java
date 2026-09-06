package Mods.create_tempratech.ThermalSystem;


public record ThermalMaterial(
        double defaultTemperatureK,
        double specificHeat,
        double density,
        double conductivity,
        double glowTemperature
) {

    /**
     * Calculates the heat capacity of a volume of material.
     *
     * @param volumeM3 volume in cubic meters
     * @return heat capacity in J/K
     */
    public double heatCapacity(double volumeM3) {
        double mass = density * volumeM3;
        return mass * specificHeat;
    }

    /**
     * Converts stored energy relative to the default temperature
     * into an absolute temperature.
     *
     * @param energyJ energy relative to the default temperature
     * @param volumeM3 volume of the block in cubic meters
     * @return temperature in Kelvin
     */
    public double temperatureFromEnergy(double energyJ, double volumeM3) {
        double capacity = heatCapacity(volumeM3);

        if (capacity <= 0.0) {
            return defaultTemperatureK;
        }

        return defaultTemperatureK + energyJ / capacity;
    }

    /**
     * Calculates the energy required to reach a temperature.
     *
     * @param temperatureK temperature in Kelvin
     * @param volumeM3 volume in cubic meters
     * @return energy relative to the default temperature in joules
     */
    public double energyFromTemperature(double temperatureK, double volumeM3) {
        return (temperatureK - defaultTemperatureK)
                * heatCapacity(volumeM3);
    }
}
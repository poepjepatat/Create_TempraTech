package Mods.create_tempratech.Network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModPayloads {

    private static final String NETWORK_VERSION = "1";

    private ModPayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);

        registrar.playToClient(
                ThermalGlowPayload.TYPE,
                ThermalGlowPayload.STREAM_CODEC,
                ThermalGlowPayload::handle
        );

        registrar.playToServer(
                ThermalProbeRequestPayload.TYPE,
                ThermalProbeRequestPayload.STREAM_CODEC,
                ThermalProbeRequestPayload::handle
        );

        registrar.playToClient(
                ThermalProbeResponsePayload.TYPE,
                ThermalProbeResponsePayload.STREAM_CODEC,
                ThermalProbeResponsePayload::handle
        );

        registrar.playToClient(
                ThermalVisualPayload.TYPE,
                ThermalVisualPayload.STREAM_CODEC,
                ThermalVisualPayload::handle
        );
    }
}

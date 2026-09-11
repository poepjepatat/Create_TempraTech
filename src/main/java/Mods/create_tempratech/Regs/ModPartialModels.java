package Mods.create_tempratech.Regs;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class ModPartialModels {

    public static final PartialModel ACTIVE_VENT_ROTOR =
            PartialModel.of(
                    ResourceLocation.fromNamespaceAndPath(
                            "create_tempratech",
                            "block/active_vent/active_vent_rotor"
                    )
            );

    private ModPartialModels() {
    }
}
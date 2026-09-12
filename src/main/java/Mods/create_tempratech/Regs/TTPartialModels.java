package Mods.create_tempratech.Regs;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class TTPartialModels {
    public static final PartialModel
        ACTIVE_LIQUID_VENT_COG = block("active_vent/inner");

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(
                Mods.create_tempratech.Create_tempratech.MODID,
                "block/" + path
        ));
    }

}

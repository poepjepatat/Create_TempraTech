package Mods.create_tempratech.Regs.Visuals;

import Mods.create_tempratech.Regs.BlockEntities.ActiveVentEntity;
import Mods.create_tempratech.Regs.ModPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.task.Plan;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import java.util.function.Consumer;

public class ActiveVentVisual
        extends KineticBlockEntityVisual<ActiveVentEntity>
        implements DynamicVisual {

    private final RotatingInstance rotor;

    public ActiveVentVisual(
            VisualizationContext context,
            ActiveVentEntity blockEntity,
            float partialTick
    ) {
        super(context, blockEntity, partialTick);

        rotor = instancerProvider()
                .instancer(
                        AllInstanceTypes.ROTATING,
                        Models.partial(ModPartialModels.ACTIVE_VENT_ROTOR)
                )
                .createInstance();

        rotor.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(rotationAxis())
                .setChanged();
    }

    @Override
    public void update(float partialTick) {
        rotor.setup(blockEntity)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(rotor);
    }

    @Override
    protected void _delete() {
        rotor.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(rotor);
    }

    @Override
    public Plan<Context> planFrame() {
        return null;
    }
}
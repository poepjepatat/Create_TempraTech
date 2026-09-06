package Mods.create_tempratech.Server;

import Mods.create_tempratech.ThermalSystem.ThermalWorld;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class AdminCommands {

    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("temperature")

                        .then(Commands.literal("add")

                                .then(Commands.argument(
                                                        "amount",
                                                        FloatArgumentType.floatArg()
                                                )

                                                .executes(context -> {

                                                    CommandSourceStack source =
                                                            context.getSource();

                                                    // Make sure the command is being
                                                    // executed by an entity/player.
                                                    Entity entity =
                                                            source.getEntity();

                                                    if (entity == null) {
                                                        source.sendFailure(
                                                                Component.literal(
                                                                        "You must be a player to use this command."
                                                                )
                                                        );

                                                        return 0;
                                                    }

                                                    // Ray-trace from the entity's eyes
                                                    // to find the block being looked at.
                                                    BlockHitResult hit =
                                                            (BlockHitResult) entity.pick(
                                                                    64.0D,
                                                                    0.0F,
                                                                    false
                                                            );

                                                    if (hit.getType() != HitResult.Type.BLOCK) {

                                                        source.sendFailure(
                                                                Component.literal(
                                                                        "You are not looking at a block."
                                                                )
                                                        );

                                                        return 0;
                                                    }

                                                    // Get the level/server world.
                                                    if (!(source.getLevel()
                                                            instanceof ServerLevel level)) {

                                                        source.sendFailure(
                                                                Component.literal(
                                                                        "This command must be run on the server."
                                                                )
                                                        );

                                                        return 0;
                                                    }

                                                    // The exact block being looked at.
                                                    BlockPos pos = hit.getBlockPos();

                                                    // Get the amount from the command.
                                                    float amount =
                                                            FloatArgumentType.getFloat(
                                                                    context,
                                                                    "amount"
                                                            );

                                                    // Change temperature.
                                                    ThermalWorld.addEnergy(level, pos, amount);

                                                    source.sendSuccess(
                                                            () -> Component.literal(
                                                                    "Energy increased by "
                                                                            + amount
                                                                            + " at "
                                                                            + pos
                                                                            + ". New temperature: "
                                                                            + amount
                                                            ),
                                                            true
                                                    );

                                                    return 1;
                                                })
                                )
                        )
        );
    }
}

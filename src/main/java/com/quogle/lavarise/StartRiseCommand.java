package com.quogle.lavarise;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class StartRiseCommand {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("start_lava_rise")
                        .requires(source -> source.hasPermission(2)) // permission level 2 (operator)
                        .executes(StartRiseCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        LavaRisingHandler.startRising();
        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Lava rising started!"), true);
        return Command.SINGLE_SUCCESS;
    }
}

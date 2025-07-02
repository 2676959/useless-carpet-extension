package uce.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.literal;

public class SleepCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("sleep")
                        .executes(context -> {
                            executeSleep(context.getSource());
                            return 1;
                        })
        );
    }

    public static void executeSleep(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            player.sleep(player.getBlockPos());
        }
    }
}

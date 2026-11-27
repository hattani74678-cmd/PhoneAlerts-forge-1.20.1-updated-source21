package com.phonealerts;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * /phonealert <type> [radius] [message]
 *   e.g. /phonealert hostile 200 Zombies at the village!
 *        /phonealert clear
 */
public class AlertCommand {

    public static void register(CommandDispatcher<CommandSourceStack> d) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("phonealert")
                .requires(s -> s.hasPermission(AlertConfig.PERMISSION_LEVEL.get()));

        for (AlertType t : AlertType.values()) {
            root.then(Commands.literal(t.id)
                    .executes(c -> run(c.getSource(), t, 100, ""))
                    .then(Commands.argument("radius", IntegerArgumentType.integer(16))
                            .executes(c -> run(c.getSource(), t, IntegerArgumentType.getInteger(c, "radius"), ""))
                            .then(Commands.argument("message", StringArgumentType.greedyString())
                                    .executes(c -> run(c.getSource(), t,
                                            IntegerArgumentType.getInteger(c, "radius"),
                                            StringArgumentType.getString(c, "message"))))));
        }
        d.register(root);
    }

    private static int run(CommandSourceStack src, AlertType t, int radius, String msg) {
        ServerPlayer p;
        try { p = src.getPlayerOrException(); }
        catch (Exception e) { src.sendFailure(Component.literal("Players only.")); return 0; }
        int n = AlertService.dispatch(p, t, t.defaultSeverity, "", msg, radius, false, true);
        src.sendSuccess(() -> Component.translatable("phonealerts.msg.sent", n), false);
        return n;
    }
}

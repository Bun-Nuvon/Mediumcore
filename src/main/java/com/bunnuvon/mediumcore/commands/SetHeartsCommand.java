package com.bunnuvon.mediumcore.commands;

import com.bunnuvon.mediumcore.Mediumcore;
import com.bunnuvon.mediumcore.PlayerObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SetHeartsCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You do not have permission to use this command.")
                    .color(Mediumcore.RED_TEXT_COLOR));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Component.text("Please specify a player.")
                    .color(Mediumcore.RED_TEXT_COLOR));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        int hearts = 10;

        if (args.length > 1) try {
            hearts = Math.round(Float.parseFloat(args[1]));
        } catch (NumberFormatException ignored) {}

        if (hearts < 0) hearts = 0;

        if (player == null) sender.sendMessage(Component.text("Player does not exist.")
                .color(Mediumcore.RED_TEXT_COLOR));
        else {
            PlayerObject object = Mediumcore.players.get(player.getUniqueId().toString());

            object.hearts = hearts;

            sender.sendMessage(Component.text(object.name)
                    .color(Mediumcore.PURPLE_TEXT_COLOR)
                    .append(Component.text(" now has ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text(hearts)
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text(" heart" + (hearts == 1 ? "" : "s"))
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(".")
                            .color(Mediumcore.YELLOW_TEXT_COLOR)));

            player.sendMessage(Component.text("Your ")
                    .color(Mediumcore.YELLOW_TEXT_COLOR)
                    .append(Component.text("hearts")
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(" have been set to ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text(hearts)
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text(".")
                            .color(Mediumcore.YELLOW_TEXT_COLOR)));

            Mediumcore.updatePlayers();
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String name = args.length > 0 ? args[0] : "";
        List<String> players = new ArrayList<>();

        if (args.length > 1 || !sender.isOp()) return players;

        for (Player player : Bukkit.getOnlinePlayers()) if (player.getName().startsWith(name)) players.add(player.getName());

        Collections.sort(players);

        return players;
    }
}
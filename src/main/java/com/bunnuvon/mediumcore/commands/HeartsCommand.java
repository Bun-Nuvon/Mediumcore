package com.bunnuvon.mediumcore.commands;

import com.bunnuvon.mediumcore.Mediumcore;
import com.bunnuvon.mediumcore.PlayerObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeartsCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player;

        if (args.length > 0) player = Bukkit.getPlayer(args[0]);
        else {
            if (sender instanceof Player) player = (Player) sender;
            else {
                sender.sendMessage(Component.text("Command cannot be used here.")
                        .color(Mediumcore.RED_TEXT_COLOR));
                return true;
            }
        }

        if (player == null) sender.sendMessage(Component.text("Player does not exist.")
                .color(Mediumcore.RED_TEXT_COLOR));
        else {
            PlayerObject p = Mediumcore.players.get(player.getUniqueId().toString());

            sender.sendMessage(Component.text(p.name)
                    .color(Mediumcore.PURPLE_TEXT_COLOR)
                    .append(Component.text(" has ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text(p.hearts)
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(" heart" + (p.hearts == 1 ? "" : "s") + ".")
                            .color(Mediumcore.YELLOW_TEXT_COLOR)));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String name = args.length > 0 ? args[0] : "";
        List<String> players = new ArrayList<>();

        if (args.length > 1) return players;

        for (Player player : Bukkit.getOnlinePlayers()) if (player.getName().startsWith(name)) players.add(player.getName());

        Collections.sort(players);

        return players;
    }
}
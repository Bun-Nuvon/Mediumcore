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

public class ReviveCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Please specify a player.")
                    .color(Mediumcore.RED_TEXT_COLOR));

            return true;
        }

        boolean canRevive = false;
        PlayerObject sacrificer = null;

        if (sender instanceof Player p) {
            PlayerObject o = Mediumcore.players.get(p.getUniqueId().toString());
            sacrificer = o;

            if (o.hearts > 1) canRevive = true;
            else sender.sendMessage(Component.text("You need at least ")
                    .color(Mediumcore.YELLOW_TEXT_COLOR)
                    .append(Component.text("2 ")
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text("hearts")
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(" to revive someone.")
                            .color(Mediumcore.YELLOW_TEXT_COLOR)));
        } else if (sender == Bukkit.getConsoleSender() || sender instanceof BlockCommandSender || sender instanceof CommandMinecart) canRevive = true;
        else sender.sendMessage(Component.text("Command cannot be used here.")
                .color(Mediumcore.RED_TEXT_COLOR));

        if (canRevive) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) sender.sendMessage(Component.text("Player does not exist.")
                    .color(Mediumcore.RED_TEXT_COLOR));
            else {
                PlayerObject object = Mediumcore.players.get(player.getUniqueId().toString());
                boolean isSacrificerNull = sacrificer == null;

                if (!isSacrificerNull) sacrificer.hearts--;

                object.hearts++;

                if (!isSacrificerNull && Objects.equals(sacrificer.uuid, object.uuid)) sender.sendMessage(Component.text("You cannot revive yourself.")
                        .color(Mediumcore.RED_TEXT_COLOR));
                else {
                    Bukkit.broadcast(Component.text(object.name)
                            .color(Mediumcore.PURPLE_TEXT_COLOR)
                            .append(Component.text(" has been " + (object.hearts == 1 ? "revived" : "given a heart") + " by ")
                                    .color(Mediumcore.YELLOW_TEXT_COLOR))
                            .append(Component.text(isSacrificerNull ? "ADMIN" : sacrificer.name)
                                    .color(Mediumcore.PURPLE_TEXT_COLOR))
                            .append(Component.text(".")
                                    .color(Mediumcore.YELLOW_TEXT_COLOR)));

                    Mediumcore.updatePlayers();
                }
            }
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
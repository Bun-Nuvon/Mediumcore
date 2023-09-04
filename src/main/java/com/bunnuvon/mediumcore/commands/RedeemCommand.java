package com.bunnuvon.mediumcore.commands;

import com.bunnuvon.mediumcore.Mediumcore;
import com.bunnuvon.mediumcore.PlayerObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RedeemCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            int hearts = 0;

            PlayerInventory inventory = player.getInventory();

            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack == null) continue;

                if (Mediumcore.isHeartContainer(itemStack)) {
                    hearts += itemStack.getAmount();
                    inventory.remove(itemStack);
                }
            }

            PlayerObject p = Mediumcore.players.get(player.getUniqueId().toString());

            p.hearts += hearts;

            if (hearts == 0) sender.sendMessage(Component.text("No ")
                    .color(Mediumcore.YELLOW_TEXT_COLOR)
                    .append(Component.text("heart containers")
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text(" to redeem.")
                            .color((Mediumcore.YELLOW_TEXT_COLOR))));
            else Bukkit.broadcast(Component.text(p.name)
                    .color(Mediumcore.PURPLE_TEXT_COLOR)
                    .append(Component.text(" redeemed ")
                        .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text(hearts)
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(" heart container" + (hearts == 1 ? "" : "s"))
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text(".")
                            .color(Mediumcore.YELLOW_TEXT_COLOR)));

            Mediumcore.updatePlayers();
        } else sender.sendMessage(Component.text("Command cannot be used here.")
                .color(Mediumcore.RED_TEXT_COLOR));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
package com.bunnuvon.mediumcore;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Mediumcore.initPlayer(event.getPlayer());
        Mediumcore.updatePlayers();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && item != null && Mediumcore.isHeartContainer(item)) {
            PlayerObject p = Mediumcore.players.get(player.getUniqueId().toString());

            p.hearts++;

            int amount = item.getAmount();

            if (amount == 1) player.getInventory().remove(item);
            else item.setAmount(--amount);

            Bukkit.broadcast(Component.text(p.name)
                    .color(Mediumcore.PURPLE_TEXT_COLOR)
                    .append(Component.text(" redeemed a ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text("heart container")
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text(".")
                            .color(Mediumcore.YELLOW_TEXT_COLOR)));

            Mediumcore.updatePlayers();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Mediumcore.playerDeath(event);
    }
}
package com.bunnuvon.mediumcore.commands;

import com.bunnuvon.mediumcore.Mediumcore;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediumcoreCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String subcommand = args.length > 0 ? args[0] : "";

        if (subcommand.equals("save")) {
            if (!sender.isOp()) {
                sender.sendMessage(Component.text("You cannot use this command.")
                        .color(Mediumcore.RED_TEXT_COLOR));
                return true;
            }

            Mediumcore.updatePlayers();
            Mediumcore.savePlayers();

            sender.sendMessage(Component.text("Players saved.")
                    .color(Mediumcore.YELLOW_TEXT_COLOR));
        } else if (subcommand.equals("refresh")) {
            if (!sender.isOp()) {
                sender.sendMessage(Component.text("You cannot use this command.")
                        .color(Mediumcore.RED_TEXT_COLOR));
                return true;
            }

            Mediumcore.refreshPlayers();
            Mediumcore.updatePlayers();

            sender.sendMessage(Component.text("Players refreshed.")
                    .color(Mediumcore.YELLOW_TEXT_COLOR));
        } else {
            PluginMeta meta = Mediumcore.plugin.getPluginMeta();

            sender.sendMessage(Component.newline()
                    .append(Component.text("——— ")
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(meta.getName())
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text(" ———")
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Version ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text(meta.getVersion())
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .appendNewline()
                    .append(Component.text("By ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text(String.join(", ", meta.getAuthors()))
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text(Objects.requireNonNull(meta.getDescription()))
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text(Objects.requireNonNull(meta.getWebsite()))
                            .color(Mediumcore.RED_TEXT_COLOR)
                            .decorate(TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, meta.getWebsite()))
                            .hoverEvent(Component.text("Click to go to Mediumcore's website.")
                                    .color(NamedTextColor.AQUA)))
                    .appendNewline()
                    .appendNewline()
                    .append(Component.text("Heart containers")
                            .color(Mediumcore.PURPLE_TEXT_COLOR))
                    .append(Component.text(" can be crafted with ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text("1")
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(" netherite ingot and ")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .append(Component.text("8")
                            .color(Mediumcore.RED_TEXT_COLOR))
                    .append(Component.text(" diamonds.")
                            .color(Mediumcore.YELLOW_TEXT_COLOR))
                    .appendNewline());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String search = args.length > 0 ? args[0] : "";
        String[] availableOptions = {
            "save",
            "refresh"
        };
        List<String> options = new ArrayList<>();

        if (args.length > 1) return options;

        if (sender.isOp()) for (String option : availableOptions) if (option.startsWith(search)) options.add(option);

        return options;
    }
}
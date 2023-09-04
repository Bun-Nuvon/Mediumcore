package com.bunnuvon.mediumcore;

import com.bunnuvon.mediumcore.commands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public final class Mediumcore extends JavaPlugin {
    public static final TextColor RED_TEXT_COLOR = TextColor.fromHexString("#ff6347");
    public static final TextColor YELLOW_TEXT_COLOR = TextColor.fromHexString("#ffde59");
    public static final NamedTextColor PURPLE_TEXT_COLOR = NamedTextColor.BLUE; // TextColor.fromHexString("#361a99")
    public static Mediumcore plugin;
    public static File dataFolder;
    public static String dataFolderPath;
    public static File playersFile;
    public static Map<String, PlayerObject> players = new HashMap<>();
    public static NamespacedKey heartContainerKey;

    @Override
    public void onEnable() {
        plugin = this;
        dataFolder = this.getDataFolder();
        dataFolderPath = this.getDataFolder().getAbsolutePath();
        playersFile = new File(dataFolderPath + "/players.json");
        heartContainerKey = new NamespacedKey(this, "heartContainer");

        Bukkit.getPluginManager().registerEvents(new Events(), this);

        refreshPlayers();

        for (Player player : Bukkit.getOnlinePlayers()) initPlayer(player);

        updatePlayers();

        Objects.requireNonNull(getCommand("deaths")).setExecutor(new DeathsCommand());
        Objects.requireNonNull(getCommand("hearts")).setExecutor(new HeartsCommand());
        Objects.requireNonNull(getCommand("mediumcore")).setExecutor(new MediumcoreCommand());
        Objects.requireNonNull(getCommand("redeem")).setExecutor(new RedeemCommand());
        Objects.requireNonNull(getCommand("revive")).setExecutor(new ReviveCommand());
        Objects.requireNonNull(getCommand("sethearts")).setExecutor(new SetHeartsCommand());

        NamespacedKey heartContainerRecipeKey = new NamespacedKey(this, "heartContainerRecipe");
        ItemStack heartContainer = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta heartContainerMeta = heartContainer.getItemMeta();

        heartContainerMeta.displayName(Component.text("Heart Container")
                .color(PURPLE_TEXT_COLOR)
                .decorate(TextDecoration.BOLD, TextDecoration.ITALIC.withState(TextDecoration.State.FALSE).decoration()));
        heartContainerMeta.lore(List.of(Component.text("Redeems one heart.")
                .color(RED_TEXT_COLOR)
                .decorate(TextDecoration.ITALIC.withState(TextDecoration.State.FALSE).decoration())));
        heartContainerMeta.setCustomModelData(33573);
        heartContainerMeta.addEnchant(Enchantment.MENDING, 0, false);

        PersistentDataContainer heartContainerContainer = heartContainerMeta.getPersistentDataContainer();

        heartContainerContainer.set(heartContainerKey, PersistentDataType.BOOLEAN, true);

        heartContainer.setItemMeta(heartContainerMeta);

        ShapedRecipe heartContainerRecipe = new ShapedRecipe(heartContainerRecipeKey, heartContainer);

        heartContainerRecipe.shape("@@@", "@-@", "@@@");
        heartContainerRecipe.setIngredient('@', Material.DIAMOND);
        heartContainerRecipe.setIngredient('-', Material.NETHERITE_INGOT);

        Bukkit.addRecipe(heartContainerRecipe);

        BukkitScheduler scheduler = this.getServer().getScheduler();

        scheduler.runTaskLater(this, Mediumcore::updatePlayers, 100);

        for (World world : Bukkit.getWorlds()) world.setDifficulty(Difficulty.HARD);
    }

    @Override
    public void onDisable() {
        savePlayers();
    }

    public static boolean isHeartContainer(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return itemStack.getType() == Material.NETHERITE_INGOT && container.has(Mediumcore.heartContainerKey) && Boolean.TRUE.equals(container.get(Mediumcore.heartContainerKey, PersistentDataType.BOOLEAN));
    }

    public static void playerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        PlayerObject p = players.get(player.getUniqueId().toString());

        if (p.hearts < 1) {
            updatePlayers();
            return;
        }

        p.deaths++;
        p.hearts--;

        Player killer = player.getKiller();

        if (killer == null) player.sendMessage(Component.text("You lost a ")
                .color(YELLOW_TEXT_COLOR)
                .append(Component.text("heart")
                        .color(RED_TEXT_COLOR))
                .append(Component.text(".")
                        .color(YELLOW_TEXT_COLOR)));
        else {
            PlayerObject k = players.get(killer.getUniqueId().toString());

            k.hearts++;

            killer.sendMessage(Component.text("You stole a ")
                    .color(YELLOW_TEXT_COLOR)
                    .append(Component.text("heart")
                            .color(RED_TEXT_COLOR))
                    .append(Component.text(" from ")
                            .color(YELLOW_TEXT_COLOR))
                    .append(Component.text(p.name)
                            .color(PURPLE_TEXT_COLOR))
                    .append(Component.text(".")
                            .color(YELLOW_TEXT_COLOR)));

            player.sendMessage(Component.text(k.name)
                    .color(PURPLE_TEXT_COLOR)
                    .append(Component.text(" stole a ")
                            .color(YELLOW_TEXT_COLOR))
                    .append(Component.text("heart")
                            .color(RED_TEXT_COLOR))
                    .append(Component.text(" from you.")
                            .color(YELLOW_TEXT_COLOR)));
        }

        if (p.hearts < 1) Bukkit.broadcast(Component.text(p.name)
                .color(PURPLE_TEXT_COLOR)
                .append(Component.text(" has been ")
                        .color(YELLOW_TEXT_COLOR))
                .append(Component.text("eliminated")
                        .color(RED_TEXT_COLOR))
                .append(Component.text(".")
                        .color(YELLOW_TEXT_COLOR)));

        updatePlayers();
    }

    public static void initPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        String name = player.getName();

        if (uuid == null) return;

        if (players.containsKey(uuid)) players.get(uuid).name = name;
        else players.put(uuid, new PlayerObject(uuid, name, 0, 10));
    }

    public static void updatePlayers() {
        for (PlayerObject p : players.values()) {
            if (p.uuid == null) continue;

            Player player = Bukkit.getPlayer(p.name);
            int hearts = p.hearts;

            if (hearts < 0) hearts = 0;

            if (player != null) {
                GameMode gameMode = player.getGameMode();

                if (gameMode == GameMode.CREATIVE) continue;

                if (hearts == 0) player.setGameMode(GameMode.SPECTATOR);
                else {
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(hearts * 2);
                    if (player.getGameMode() != GameMode.ADVENTURE) player.setGameMode(GameMode.SURVIVAL);
                }
            }
        }
    }

    public static void savePlayers() {
        JSONArray array = new JSONArray();

        for (PlayerObject player : players.values()) if (player.uuid != null) array.add(player.export());

        String json = array.toJSONString();

        try {
            FileWriter writer = new FileWriter(playersFile);

            writer.write(json);

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void refreshPlayers() {
        if (!dataFolder.exists()) dataFolder.mkdir();
        if (!playersFile.exists()) {
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            FileReader reader = new FileReader(playersFile);
            StringBuilder builder = new StringBuilder();
            int ch;

            while ((ch = reader.read()) != -1) builder.append((char) ch);

            String json = builder.toString();

            if (json.isEmpty()) {
                FileWriter writer = new FileWriter(playersFile);

                writer.append("[]");

                writer.close();

                json = "[]";
            }

            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(json);

            players.clear();

            for (Object object : array) {
                JSONObject player = (JSONObject) object;
                PlayerObject p = new PlayerObject(player.get("uuid").toString(), player.get("name").toString(), ((Long) player.get("deaths")).intValue(), ((Long) player.get("hearts")).intValue());
                players.put(p.uuid, p);
            }

            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
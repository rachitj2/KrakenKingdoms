package me.rachit.krakenmc;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class KingdomPlugin extends JavaPlugin {

    private File kingdomsFiles;
    private File playersFile;
    private FileConfiguration kingdomsConfig;
    private FileConfiguration playersConfig;
    private final Map<String, Kingdom> kingdoms = new HashMap<>();
    private final Map<UUID, String> playerKingdoms = new HashMap<>();
    private final Map<UUID, String> pendingInvites = new HashMap<>();
    private final Map<UUID, Integer> krakenPoints = new HashMap<>();

    @Override
    public void onEnable() {

        kingdomsFiles = new File(getDataFolder(), "kingdoms.yml");
        playersFile = new File(getDataFolder(), "players.yml");

        if (!kingdomsFiles.exists()) {
            kingdomsFiles.getParentFile().mkdirs();
            saveResource("kingdoms.yml", false);
        }

        if (!playersFile.exists()) {
            saveResource("players.yml", false);
        }

        kingdomsConfig = YamlConfiguration.loadConfiguration(kingdomsFiles);
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);

        loadPlayers();
        loadKingdoms();

        saveDefaultConfig();

        getLogger().info("KrakenKingdom has been activated and sole is happy");
        getCommand("kingdom").setExecutor(new KingdomCommand(this));
        getCommand("krakenpoints").setExecutor(new KingdomCommand(this));

        int amount = getConfig().getInt("points.amount");
        long interval = getConfig().getLong("points.interval-minutes") * 60 * 20L;

        getServer().getScheduler().runTaskTimer(this, () -> {

            getServer().getOnlinePlayers().forEach(player -> {

                krakenPoints.put(
                        player.getUniqueId(),
                        krakenPoints.getOrDefault(player.getUniqueId(), 0) + amount

                );
                player.sendMessage(ChatColor.AQUA + "+ " + amount +
                        " Kraken Point(s)! Total: " +
                        ChatColor.GOLD +
                        krakenPoints.get(player.getUniqueId())
                );
            });
        }, interval, interval);

    }

    @Override
    public void onDisable() {

        savePlayers();
        saveKingdoms();

        getLogger().info("Plugin disabled and sloth is going berserk");
    }

    public void savePlayers() {

        for (UUID uuid : krakenPoints.keySet()) {

            playersConfig.set(
                    "players." + uuid + ".points",
                    krakenPoints.get(uuid)
            );
        }

        try {
            playersConfig.save(playersFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlayers() {

        if (!playersConfig.contains("players")) {
            return;
        }

        for (String uuidString :
                playersConfig.getConfigurationSection("players").getKeys(false)) {

            UUID uuid = UUID.fromString(uuidString);

            int points = playersConfig.getInt(
                    "players." + uuidString + ".points"
            );

            krakenPoints.put(uuid, points);
        }
    }

    public void saveKingdoms () {

    }

    public void loadKingdoms () {

    }

    public Map<String, Kingdom> getKingdoms(){
        return kingdoms;
    }

    public Map<UUID, String> getPlayerKingdoms(){
        return playerKingdoms;
    }

    public Map<UUID, String> getPendingInvites() {
        return pendingInvites;
    }

    public Map<UUID, Integer> getKrakenPoints() {
        return krakenPoints;
    }
}

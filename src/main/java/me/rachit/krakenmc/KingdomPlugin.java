package me.rachit.krakenmc;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
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
    private final Map<UUID, Location> pendingTeleports = new HashMap<>();
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
        getCommand("kraken").setExecutor(new KingdomCommand(this));


        getServer().getPluginManager().registerEvents(
                new TeleportListener(this),
                this
        );

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

        getLogger().info("Kingdoms in memory: " + kingdoms.size());

        kingdomsConfig.set("kingdoms", null);

        for (Kingdom kingdom : kingdoms.values()) {

            String path = "kingdoms." + kingdom.getName();

            kingdomsConfig.set(path + ".owner",
                    kingdom.getOwner().toString());

            for (Map.Entry<UUID, KingdomRank> entry :
                kingdom.getMembers().entrySet()) {

                kingdomsConfig.set(
                        path + ".members." + entry.getKey(),
                        entry.getValue().name()
                );
            }

            kingdomsConfig.set(
                    path + ".claimBlocks",
                    kingdom.getClaimBlocks()
            );

            kingdomsConfig.set(
                    path + ".damageBonus",
                    kingdom.getDamageBonus()
            );

            if (kingdom.getHome() !=null) {
                kingdomsConfig.set(path + ".home",
                        kingdom.getHome());

            }

            for (Map.Entry<String, Location> warp :
                kingdom.getWarps().entrySet()) {

                kingdomsConfig.set(
                        path + ".warps." + warp.getKey(),
                        warp.getValue()
                );
            }
        }

        try {
            kingdomsConfig.save(kingdomsFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadKingdoms() {

        if (!kingdomsConfig.contains("kingdoms")) {
            return;
        }

        for (String kingdomName :
                kingdomsConfig.getConfigurationSection("kingdoms").getKeys(false)) {

            String path = "kingdoms." + kingdomName;

            UUID owner = UUID.fromString(
                    kingdomsConfig.getString(path + ".owner")
            );

            Kingdom kingdom = new Kingdom(kingdomName, owner);

            if (kingdomsConfig.contains(path + ".members")) {

                for (String uuidString :
                        kingdomsConfig.getConfigurationSection(path + ".members").getKeys(false)) {

                    UUID uuid = UUID.fromString(uuidString);

                    KingdomRank rank = KingdomRank.valueOf(
                            kingdomsConfig.getString(
                                    path + ".members." + uuidString
                            )
                    );

                    kingdom.setClaimBlocks(
                            kingdomsConfig.getInt(path + ".claimBlocks")
                    );

                    kingdom.setDamageBonus(
                            kingdomsConfig.getDouble(path + ".damageBonus")
                    );

                    kingdom.setRank(uuid, rank);
                    playerKingdoms.put(uuid, kingdomName.toLowerCase());
                }
            }

            if (kingdomsConfig.contains(path + ".home")) {

                Location home = kingdomsConfig.getLocation(path + ".home");
                kingdom.setHome(home);
            }

            if (kingdomsConfig.contains(path + ".warps")) {

                for (String warpName :
                        kingdomsConfig.getConfigurationSection(path + ".warps").getKeys(false)) {

                    Location warpLoc = kingdomsConfig.getLocation(
                            path + ".warps." + warpName
                    );

                    kingdom.setWarps(warpName, warpLoc);
                }
            }

            kingdoms.put(
                    kingdomName.toLowerCase(),
                    kingdom
            );
        }
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

    public Map<UUID, Location> getPendingTeleports() {
        return pendingTeleports;
    }
}

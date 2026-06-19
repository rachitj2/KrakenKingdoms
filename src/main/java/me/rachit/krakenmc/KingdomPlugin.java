package me.rachit.krakenmc;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KingdomPlugin extends JavaPlugin {

    private final Map<String, Kingdom> kingdoms = new HashMap<>();
    private final Map<UUID, String> playerKingdoms = new HashMap<>();
    private final Map<UUID, String> pendingInvites = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("KrakensMC plugin has been activated and sole is happy");
        getCommand("kingdom").setExecutor(new KingdomCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled and sloth is going berserk");
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
}

package me.rachit.krakenmc;

import org.bukkit.plugin.java.JavaPlugin;

public class KingdomPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("KrakensMC plugin has been activated and sole is happy");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled and sloth is going berserk");
    }
}

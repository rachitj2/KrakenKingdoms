package me.rachit.krakenmc;

import org.bukkit.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Kingdom {

    private Location home;
    private int level;
    private double damageBonus;
    private final Map<String, Integer> upgrades;
    private final Map<String, Location> warps = new HashMap<>();
    private final String name;
    private final UUID owner;
    private final Map<UUID, KingdomRank> members;

    public Kingdom(String name, UUID owner) {
        this.name = name;
        this.upgrades = new HashMap<>();
        this.owner = owner;
        this.members = new HashMap<>();

        members.put(owner, KingdomRank.OWNER);
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Map<UUID, KingdomRank> getMembers() {
        return members;
    }

    public KingdomRank getRank(UUID player) {
        return members.get(player);
    }

    public void setRank(UUID player, KingdomRank rank){
        members.put(player, rank);
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public Map<String, Location> getWarps() {
        return warps;
    }

    public void setWarps(String name, Location location) {
        warps.put(name.toLowerCase(), location);
    }

    public Location getWarps(String name) {
        return warps.get(name.toLowerCase());
    }

    public void removeWarp(String name) {
        warps.remove(name.toLowerCase());
    }
}



package me.rachit.krakenmc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Kingdom {

    private final String name;
    private final UUID owner;
    private final Map<UUID, KingdomRank> members;

    public Kingdom(String name, UUID owner) {
        this.name = name;
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

}



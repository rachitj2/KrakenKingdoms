package me.rachit.krakenmc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kingdom {

    private final String name;
    private final UUID owner;
    private final List<UUID> members;

    public Kingdom(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getMembers() {
        return members;
    }


}



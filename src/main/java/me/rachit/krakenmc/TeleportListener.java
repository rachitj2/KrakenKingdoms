package me.rachit.krakenmc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TeleportListener implements Listener {

    private final KingdomPlugin plugin;

    public TeleportListener(KingdomPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        if (!plugin.getPendingTeleports().containsKey(
                player.getUniqueId())) {
            return;
        }

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        if (from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ()) {

            plugin.getPendingTeleports().remove(
                    player.getUniqueId()
            );

            player.sendMessage(
                    ChatColor.RED +
                            "Teleport cancelled because you moved."
            );
        }
    }
}
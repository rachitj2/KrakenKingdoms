package me.rachit.krakenmc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class KingdomCommand implements CommandExecutor {

        private final KingdomPlugin plugin;

        public KingdomCommand(KingdomPlugin plugin) {
            this.plugin = plugin;
        }

    private void broadcastToKingdom(Kingdom kingdom, String message) {
        for(UUID memberId : kingdom.getMembers().keySet()) {
            Player member = Bukkit.getPlayer(memberId);

            if (member != null) {
                member.sendMessage(message);
            }
        }
    }


    @Override
        public boolean onCommand(CommandSender sender, Command command, String label , String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only online players can run this command.");
            return true;
        }

        // ===================
        // Kraken points
        // ===================

        if (command.getName().equalsIgnoreCase("krakenpoints")) {

            int points = plugin.getKrakenPoints()
                    .getOrDefault(player.getUniqueId(), 0);

            player.sendMessage(
                    ChatColor.GOLD + "===== Kraken points ===== "
            );

            player.sendMessage(
                    ChatColor.YELLOW + "Balance: "
                            + ChatColor.AQUA + points

            );
            return true;
        }


            // ====================
            // Kingdom Help
            // ====================

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "/kingdom help");
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {

                player.sendMessage(ChatColor.GOLD + "----- Kingdom Commands -----");
                player.sendMessage(ChatColor.YELLOW + "/kingdom create <name>");
                player.sendMessage(ChatColor.YELLOW + "/kingdom delete");
                player.sendMessage(ChatColor.YELLOW + "/kingdom invite <user>");
                player.sendMessage(ChatColor.YELLOW + "/kingdom members");
                player.sendMessage(ChatColor.YELLOW + "/kingdom help");
                player.sendMessage(ChatColor.YELLOW + "/kingdom info");
                player.sendMessage(ChatColor.YELLOW + "/kingdom version");
                player.sendMessage(ChatColor.YELLOW + "/kingdom join");
                player.sendMessage(ChatColor.YELLOW + "/kingdom promote <player>");
                player.sendMessage(ChatColor.YELLOW + "/kingdom demote <player>");
                player.sendMessage(ChatColor.YELLOW + "/kingdom leave");
                player.sendMessage(ChatColor.YELLOW + "/kingdom members");
                return true;
            }


            // ====================
            // Kingdom Info
            // ====================

            if (args[0].equalsIgnoreCase("info")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                player.sendMessage(ChatColor.RED + "===== Kingdom Info ====");
                player.sendMessage(ChatColor.YELLOW + "Name: "
                            + ChatColor.DARK_AQUA + kingdom.getName());

                player.sendMessage(ChatColor.YELLOW + "Owner: "
                        + ChatColor.DARK_AQUA
                        + Bukkit.getOfflinePlayer(kingdom.getOwner()).getName());

                player.sendMessage(ChatColor.YELLOW + "Members: "
                        + ChatColor.DARK_AQUA
                        + (kingdom.getMembers().size() + 1));
                return true;
            }


            // ====================
            // Kingdom Version
            // ====================

            if (args[0].equalsIgnoreCase("Version")) {
                player.sendMessage(ChatColor.GOLD + "==== KrakenKingdoms ====");
                player.sendMessage(ChatColor.YELLOW + "Version: "
                        + ChatColor.DARK_AQUA + plugin.getDescription().getVersion());

                player.sendMessage(ChatColor.YELLOW + "Author: "
                        + ChatColor.DARK_AQUA + "Rxyc");
                return true;

            }

            // ===================
            // Kingdom Points ( krakenpoints)
            // ===================

            if (command.getName().equalsIgnoreCase("krakenpoints")) {

                int points = plugin.getKrakenPoints()
                        .getOrDefault(player.getUniqueId(), 0);

                player.sendMessage(
                        ChatColor.AQUA + "You currently have "
                         + ChatColor.GOLD + points
                         + ChatColor.AQUA + " Kraken points"
                );

                return true;
            }


            // ====================
            // Kingdom Invite
            // ====================

            if (args[0].equalsIgnoreCase("invite")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom!");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                if (!kingdom.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only the kingdom owner can invite players.");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /kingdom invite <player>");
                    return true;
                }

                Player target = getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage((ChatColor.RED + "The player is not online."));
                    return true;
                }

                if (plugin.getPlayerKingdoms().containsKey(target.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "That player is already in a kingdom");
                    return true;
                }

                plugin.getPendingInvites().put(target.getUniqueId(), kingdomName);

                player.sendMessage(
                        ChatColor.GREEN + "Invited "
                        + ChatColor.YELLOW + target.getName()
                        + ChatColor.GREEN + " to your Kingdom!"
                );

                target.sendMessage(
                        ChatColor.GOLD + "You have been invited to join "
                        + ChatColor.DARK_AQUA + kingdomName
                );

                target.sendMessage(
                        ChatColor.YELLOW + "Use "
                         + ChatColor.AQUA + "/kingdom join"
                         + ChatColor.YELLOW + " to accept"
                );

                return true;
            }


            // ====================
            // Kingdom Join
            // ====================

            if (args[0].equalsIgnoreCase("join")) {

                if (plugin.getPlayerKingdoms().containsKey(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You are already in a kingdom.");
                    return true;
                }

                String kingdomName = plugin.getPendingInvites().get(player.getUniqueId());

                if (kingdomName == null) {
                    player.sendMessage(ChatColor.RED + "You do not have any pending kingdom invite");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                if (kingdom == null) {
                    player.sendMessage(ChatColor.RED + "That kingdom no longer exists");
                    plugin.getPendingInvites().remove(player.getUniqueId());
                    return true;
                }

                kingdom.getMembers().put(player .getUniqueId(), KingdomRank.MEMBER);

                plugin.getPlayerKingdoms().put(player.getUniqueId(), kingdomName);

                plugin.getPendingInvites().remove(player.getUniqueId());

                broadcastToKingdom(
                        kingdom,
                        ChatColor.GOLD + "[Kingdom] "
                            + ChatColor.YELLOW + player.getName()
                            + ChatColor.GREEN + " has joined the kingdom."
                );
                return true;

            }


            // ====================
            // Kingdom Promote
            // ====================

            if (args[0].equalsIgnoreCase("promote")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName == null) {
                    player.sendMessage(ChatColor.RED + "You must be in a kingdom to run this command");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                if (kingdom.getRank(player.getUniqueId()) != KingdomRank.OWNER) {
                    player.sendMessage(ChatColor.RED + "You must be the owner to promote others");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /kingdom promote <player>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
                }

                if (!kingdom.getMembers().containsKey(target.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "That player is not in your kingdom.");
                    return true;
                }

                if (kingdom.getRank(target.getUniqueId()) == KingdomRank.ADMIN) {
                    player.sendMessage(ChatColor.RED + "That player is already an admin");
                    return true;
                }

                if (target.getUniqueId().equals(kingdom.getOwner())) {
                    player.sendMessage(ChatColor.RED + "You cannot promote the kingdom owner.");
                    return true;
                }

                kingdom.setRank(target.getUniqueId(), KingdomRank.ADMIN);

                broadcastToKingdom(
                        kingdom,
                        ChatColor.GREEN + "[Kingdom] "
                         + ChatColor.YELLOW + target.getName()
                         + ChatColor.GREEN + " has been promoted to Admin!"
                );

                target.sendMessage(
                        ChatColor.GREEN + "You have been promoted to admin in "
                         + ChatColor.DARK_AQUA + kingdom.getName()
                );

                return true;
            }


            // ====================
            // Kingdom Demote
            // ====================

            if (args[0].equalsIgnoreCase("demote")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom!");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                if (kingdom.getRank(player.getUniqueId()) != KingdomRank.OWNER) {
                    player.sendMessage(ChatColor.RED + "Only the owner can demote members.");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /kingdom demote <player>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                    return true;
                }

                if (!kingdom.getMembers().containsKey(target.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "That player is not in your kingdom.");
                    return true;
                }

                if (kingdom.getRank(target.getUniqueId()) != KingdomRank.ADMIN) {
                    player.sendMessage(ChatColor.RED + "That player is not an admin.");
                    return true;
                }

                if (target.getUniqueId().equals(kingdom.getOwner())) {
                    player.sendMessage(ChatColor.RED + "You cannot demote the owner.");
                    return true;
                }

                kingdom.setRank(target.getUniqueId(), KingdomRank.MEMBER);

                broadcastToKingdom(
                        kingdom,
                        ChatColor.GREEN + "[Kingdom] "
                                + ChatColor.YELLOW + target.getName()
                                + ChatColor.GREEN + " has been demoted to Member."
                );

                target.sendMessage(
                        ChatColor.RED + "You have been demoted to Member in "
                                + ChatColor.DARK_AQUA + kingdom.getName()
                );

                return true;
            }


            // ====================
            // Kingdom Leave
            // ====================

            if (args[0].equalsIgnoreCase("leave")) {

                String kindomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kindomName ==null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kindomName.toLowerCase());

                if (kingdom.getRank(player.getUniqueId()) == KingdomRank.OWNER) {
                    player.sendMessage(
                            ChatColor.RED + "Use /kingdom delete instead , owner cannot leave."
                    );
                    return true;
                }

                kingdom.getMembers().remove(player.getUniqueId());
                plugin.getPlayerKingdoms().remove(player.getUniqueId());

                broadcastToKingdom(
                        kingdom,
                        ChatColor.GOLD + "[Kingdom] "
                               + ChatColor.YELLOW + player.getName()
                               + ChatColor.GOLD + " left the kingdom"
                );

                player.sendMessage(
                        ChatColor.GREEN + "You have left "
                         + ChatColor.DARK_AQUA + kindomName
                         + ChatColor.GREEN + "."
                );
                return true;
            }


            // ====================
            // Kingdom Kick
            // ====================

            if (args[0].equalsIgnoreCase("kick")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom!");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                KingdomRank rank = kingdom.getRank(player.getUniqueId());

                if (rank !=KingdomRank.OWNER && rank != KingdomRank.ADMIN) {
                    player.sendMessage(ChatColor.RED + "You must be an admin or owner to kick members from your kingdom!");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /kingdom kick <player>");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found");
                    return true;
                }

                if (!kingdom.getMembers().containsKey(target.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "That player is not in your kingdom");
                    return true;
                }

                if (kingdom.getRank(target.getUniqueId()) == KingdomRank.OWNER) {
                    player.sendMessage(ChatColor.RED + "You cannot kick the owner");
                    return true;
                }

                if (rank == KingdomRank.ADMIN
                        && kingdom.getRank(target.getUniqueId()) == KingdomRank.ADMIN) {
                    player.sendMessage(ChatColor.RED + "Admins cannot kick other admins!");
                    return true;
                }

                kingdom.getMembers().remove(target.getUniqueId());
                plugin.getPlayerKingdoms().remove(target.getUniqueId());

                broadcastToKingdom(
                        kingdom,
                        ChatColor.GREEN + "[Kingdom] "
                         + ChatColor.YELLOW + target.getName()
                         + ChatColor.GREEN + " has been kicked from the kingdom."
                );

                target.sendMessage(
                        ChatColor.BLUE + "You have been kicked from "
                         + ChatColor.DARK_AQUA + kingdom.getName()
                );

                return true;

            }


            // ====================
            // Kingdom Members
            // ====================

            if (args[0].equalsIgnoreCase("members")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName ==null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                player.sendMessage(ChatColor.GOLD + "===== " + kingdom.getName() + " Members =====");

                String ownerName = Bukkit.getOfflinePlayer(kingdom.getOwner()).getName();

                player.sendMessage(
                        ChatColor.GOLD + "[OWNER]"
                                + ChatColor.YELLOW + ownerName
                );

                for (UUID memberId : kingdom.getMembers().keySet()) {

                    if (memberId.equals(kingdom.getOwner())) {
                        continue;
                    }

                    String name = Bukkit.getOfflinePlayer(memberId).getName();
                    KingdomRank rank = kingdom.getRank(memberId);

                    ChatColor rankColor = switch (rank) {
                        case ADMIN -> ChatColor.RED;
                        case MEMBER -> ChatColor.GRAY;
                        default -> ChatColor.WHITE;
                    };

                    player.sendMessage(
                            rankColor + "[" + rank + "] "
                                        + ChatColor.YELLOW + name
                    );
                }
                return true;
            }

            // ===================
            // Kingdom SetHome
            // ===================

            if (args[0].equalsIgnoreCase("sethome")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom.");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                KingdomRank rank = kingdom.getRank(player.getUniqueId());

                if (rank != KingdomRank.OWNER && rank != KingdomRank.ADMIN) {
                    player.sendMessage(ChatColor.RED + "You must be an admin or above to use this command.");
                    return true;
                }

                kingdom.setHome(player.getLocation());

                player.sendMessage(
                        ChatColor.GREEN + "Kingdom home has been set to your current location"
                );

                return true;

            }

            // ===================
            // Kingdom DelHome
            // ===================

        if (args[0].equalsIgnoreCase("delhome")) {

            String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

            if (kingdomName ==null) {
                player.sendMessage(ChatColor.RED + "You are not in a kingdom");
                return true;
            }

            Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

            if (kingdom.getRank(player.getUniqueId()) != KingdomRank.OWNER) {
                player.sendMessage(ChatColor.RED + "Only owner can delete homes");
                return true;
            }

            kingdom.setHome(null);

            player.sendMessage(ChatColor.GREEN + "Home has been deleted.");
            return true;
        }


            // ===================
            // Kingdom Home
            // ===================

            if (args[0].equalsIgnoreCase("home")) {

                String kingdomName =plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName ==null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                if (kingdom.getHome() == null) {
                    player.sendMessage(ChatColor.RED + "Your kingdom does not have a home set");
                    return true;
                }

                int delay = plugin.getConfig().getInt("teleport.home-delay");

                player.sendMessage(
                        ChatColor.YELLOW + "Teleporting to kingdom home in "
                         + delay + " seconds..."
                );

                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> player.teleport(kingdom.getHome()),
                        delay * 20L
                );

                return true;
            }

            // ====================
            // Kingdom Delwarp
            // ====================

            if (args[0].equalsIgnoreCase("delwarp")) {

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /kingdom delwarp <name>");
                    return true;
                }

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName ==null) {
                    player.sendMessage(ChatColor.RED + "You must be in a kingdom to run this command");
                    return true;
                }
                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                if (kingdom.getRank(player.getUniqueId()) != KingdomRank.OWNER) {
                    player.sendMessage(ChatColor.RED + "Only the owner can delete warps");
                    return true;
                }

                String warpName = args[1].toLowerCase();

                if (!kingdom.getWarps().containsKey(warpName)) {
                    player.sendMessage(ChatColor.RED + "Warp does not exist.");
                    return true;
                }

                kingdom.getWarps().remove(warpName);

                player.sendMessage(ChatColor.GREEN + "Deleted the warp successfully!");
                return true;
            }


            // ====================
            // Kingdom SetWarp
            // ====================

            if (args[0].equalsIgnoreCase("setwarp")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName ==null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());
                KingdomRank rank = kingdom.getRank(player.getUniqueId());

                if (rank !=KingdomRank.OWNER && rank != KingdomRank.ADMIN) {
                    player.sendMessage(ChatColor.RED + "You dont have permission to run this command");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /kingdom setwarp <name>");
                    return true;
                }

                int maxWarps = plugin.getConfig().getInt("max-warps");

                if (!kingdom.getWarps().containsKey(args[1].toLowerCase())
                    && kingdom.getWarps().size() >= maxWarps) {

                    player.sendMessage(ChatColor.RED + "Your kingdom has reached the maximum number of warps");
                    return true;
                }

                kingdom.setWarps(args[1], player.getLocation());

                player.sendMessage(
                        ChatColor.GREEN + "Warp "
                         + ChatColor.YELLOW + args[1]
                         + ChatColor.GREEN + " has been set to your current location!"

                );
                return true;

            }

            // ====================
            // Kingdom Warp
            // ====================

            if (args[0].equalsIgnoreCase("warp")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName ==null) {
                    player.sendMessage(ChatColor.RED + "You are not in a kingdom");
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /kingdsom warp <name>");
                    return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                Location warp = kingdom.getWarps(args[1]);

                if (warp ==null) {
                    player.sendMessage(ChatColor.RED + "That warp does not exist.");
                    return true;
                }

                int delay = plugin.getConfig().getInt("teleport.warp-delay");

                player.sendMessage(
                        ChatColor.YELLOW + "Teleporting in "
                         + delay + " seconds..."

                );

                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> player.teleport(warp),
                        delay * 20L
                );
                return true;
            }

            // ====================
            // Kingdom Delete
            // ====================

            if (args[0].equalsIgnoreCase("delete")) {

                String kingdomName = plugin.getPlayerKingdoms().get(player.getUniqueId());

                if (kingdomName == null) {
                   player.sendMessage(ChatColor.RED + "You are Not in a Kingdom");
                   return true;
                }

                Kingdom kingdom = plugin.getKingdoms().get(kingdomName.toLowerCase());

                if (!kingdom.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only the owner can delete the kingdom");
                    return true;
                }

                for (UUID member : kingdom.getMembers().keySet()) {
                    plugin.getPlayerKingdoms().remove(member);
                }

                plugin.getKingdoms().remove(kingdomName.toLowerCase());

                player.sendMessage(
                        ChatColor.RED + "Kingdom "
                                  + ChatColor.DARK_AQUA + kingdomName
                                  + ChatColor.RED + " has been deleted"
                );

                return true;
            }



            // ====================
            // Kingdom Create
            // ====================

            if (!args[0].equalsIgnoreCase("create")) {
                player.sendMessage(ChatColor.RED + "Unknown Command");
                return true;
            }

            if (plugin.getPlayerKingdoms().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are already in a kingdom.");
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /kingdom create <name>");
                return true;
            }

            String kingdomName = args[1];

            if (plugin.getKingdoms().containsKey(kingdomName.toLowerCase())) {
                player.sendMessage(ChatColor.DARK_RED + "That kingdom already exists.");
                return true;
            }

            Kingdom kingdom = new Kingdom(kingdomName, player.getUniqueId());

            plugin.getKingdoms().put(kingdomName.toLowerCase(), kingdom);
            plugin.getPlayerKingdoms().put(player.getUniqueId(), kingdomName);

            player.sendMessage(
                    ChatColor.YELLOW + "Kingdom "
                            + ChatColor.DARK_AQUA + kingdomName
                            + ChatColor.YELLOW + " Created Successfully!"
            );
            return true;
        }
    }



package me.rachit.krakenmc;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;

public class KingdomCommand implements CommandExecutor {

        private final KingdomPlugin plugin;

        public KingdomCommand(KingdomPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label , String[] args) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Only online players can run this command.");
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

                player.sendMessage(
                        ChatColor.GREEN + "You have joined "
                         + ChatColor.DARK_AQUA + kingdomName
                         + ChatColor.GREEN + "!"
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

                kingdom.setRank(target.getUniqueId(), KingdomRank.ADMIN);

                player.sendMessage(
                        ChatColor.GREEN + "Promoted "
                         + ChatColor.YELLOW + target.getName()
                         + ChatColor.GREEN + " to Admin!"
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

                kingdom.setRank(target.getUniqueId(), KingdomRank.MEMBER);

                player.sendMessage(
                        ChatColor.GREEN + "Demoted "
                                + ChatColor.YELLOW + target.getName()
                                + ChatColor.GREEN + " to Member."
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

                player.sendMessage(
                        ChatColor.GREEN + "You have left "
                         + ChatColor.DARK_AQUA + kindomName
                         + ChatColor.GREEN + "."
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



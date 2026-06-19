package me.rachit.krakenmc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.UUID;

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

                return true;
            }

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

                plugin.getKingdoms().remove(kingdomName.toLowerCase());
                plugin.getPlayerKingdoms().remove(player.getUniqueId());

                player.sendMessage(
                        ChatColor.RED + "Kingdom "
                                  + ChatColor.DARK_AQUA + kingdomName
                                  + ChatColor.RED + " has been deleted"
                );

                return true;
            }

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



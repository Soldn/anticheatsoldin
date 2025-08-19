package com.sldn.sldnsoldin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование:");
            sender.sendMessage(ChatColor.YELLOW + "/sldn ban <игрок>");
            sender.sendMessage(ChatColor.YELLOW + "/sldn kick <игрок>");
            return true;
        }

        String action = args[0];
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок " + args[1] + " не найден.");
            return true;
        }

        switch (action.toLowerCase()) {
            case "ban": {
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(target.getName(), "Забанен читом", null, "SLDNSoldin");
                target.kickPlayer("Вы забанены без причины.");
                Bukkit.broadcastMessage(ChatColor.RED + "[BAN] " + target.getName() + " был забанен без причины.");
                break;
            }

            case "kick": {
                target.kickPlayer("Кикнут без причины.");
                Bukkit.broadcastMessage(ChatColor.GOLD + "[KICK] " + target.getName() + " был кикнут без причины.");
                break;
            }

            default:
                sender.sendMessage(ChatColor.RED + "Неизвестное действие: " + action);
                break;
        }
        return true;
    }
}

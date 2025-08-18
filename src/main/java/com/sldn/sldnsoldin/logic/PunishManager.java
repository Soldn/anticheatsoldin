package com.sldn.sldnsoldin.commands;

import com.sldn.sldnsoldin.logic.PunishManager;
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
            sender.sendMessage(ChatColor.YELLOW + "/sldn ban <игрок> <причина>");
            sender.sendMessage(ChatColor.YELLOW + "/sldn tempban <игрок> <минуты> <причина>");
            sender.sendMessage(ChatColor.YELLOW + "/sldn kick <игрок> <причина>");
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
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Использование: /sldn ban <игрок> <причина>");
                    return true;
                }
                String reason = String.join(" ", args).replaceFirst("ban " + args[1] + " ", "");
                PunishManager.ban(target, reason);
                sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " забанен.");
                break;
            }

            case "tempban": {
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.RED + "Использование: /sldn tempban <игрок> <минуты> <причина>");
                    return true;
                }
                int minutes;
                try {
                    minutes = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Укажите число минут!");
                    return true;
                }
                String reason = String.join(" ", args).replaceFirst("tempban " + args[1] + " " + args[2] + " ", "");
                PunishManager.tempBan(target, reason, minutes);
                sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " временно забанен на " + minutes + " мин.");
                break;
            }

            case "kick": {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Использование: /sldn kick <игрок> <причина>");
                    return true;
                }
                String reason = String.join(" ", args).replaceFirst("kick " + args[1] + " ", "");
                PunishManager.kick(target, reason);
                sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " кикнут.");
                break;
            }

            default:
                sender.sendMessage(ChatColor.RED + "Неизвестное действие: " + action);
                break;
        }

        return true;
    }
}

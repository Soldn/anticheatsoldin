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
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Использование: /sldn <test|ban|tempban|kick>");
            return true;
        }

        if (args[0].equalsIgnoreCase("test") && sender instanceof Player) {
            PunishManager.playPunishAnimation((Player) sender);
            sender.sendMessage(ChatColor.GREEN + "Тест анимации!");
            return true;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Использование: /sldn ban <игрок> <причина>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
                PunishManager.ban(target, reason);
                sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " забанен. Причина: " + reason);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("tempban")) {
            if (args.length < 5) {
                sender.sendMessage(ChatColor.RED + "Использование: /sldn tempban <игрок> <число> <s/m/h/d> <причина>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                int duration = Integer.parseInt(args[2]);
                String unit = args[3];
                String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 4, args.length));
                PunishManager.tempBan(target, reason, duration, unit);
                sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " временно забанен!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Использование: /sldn kick <игрок> <причина>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
                PunishManager.kick(target, reason);
                sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName() + " кикнут.");
            }
            return true;
        }

        return true;
    }
}

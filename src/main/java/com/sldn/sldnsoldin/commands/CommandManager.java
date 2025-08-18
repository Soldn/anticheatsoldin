package com.sldn.sldnsoldin.commands;

import com.sldn.sldnsoldin.SLDNSoldin;
import com.sldn.sldnsoldin.utils.PunishManager;
import com.sldn.sldnsoldin.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {

    private final SLDNSoldin plugin;

    public CommandManager(SLDNSoldin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName().toLowerCase();

        try {
            switch (name) {
                case "sldn":
                    return handleSldn(sender, args);
                case "ban":
                    return handleBan(sender, args);
                case "unban":
                    return handleUnban(sender, args);
                case "tempban":
                    return handleTempban(sender, args);
                default:
                    sender.sendMessage(ChatColor.RED + "Неизвестная команда.");
                    return true;
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Ошибка: " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    private boolean handleSldn(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "SLDNSoldin v1.1.0");
        sender.sendMessage(ChatColor.YELLOW + "/ban <ник> [причина]");
        sender.sendMessage(ChatColor.YELLOW + "/unban <ник>");
        sender.sendMessage(ChatColor.YELLOW + "/tempban <ник> <время> [причина]  (например: 30m, 2h, 7d)");
        sender.sendMessage(ChatColor.YELLOW + "/sldn logs <ник>  - показать логи");
        return true;
    }

    private boolean handleBan(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Использование: /ban <ник> [причина]");
            return true;
        }
        String targetName = args[0];
        String reason = args.length >= 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Нарушение правил/читы";

        OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);
        PunishManager pm = plugin.getPunishManager();
        pm.permBan(off, reason, sender.getName(), true);
        return true;
    }

    private boolean handleUnban(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Использование: /unban <ник>");
            return true;
        }
        String targetName = args[0];
        OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);
        plugin.getPunishManager().unban(off, sender.getName());
        return true;
    }

    private boolean handleTempban(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Использование: /tempban <ник> <время> [причина]");
            return true;
        }
        String targetName = args[0];
        String timeSpec = args[1];
        String reason = args.length >= 3 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "Временный бан (подозрение на читы)";
        long millis = Text.parseDurationMillis(timeSpec);
        if (millis <= 0) {
            sender.sendMessage(ChatColor.RED + "Неверное время. Примеры: 30m, 2h, 7d");
            return true;
        }
        OfflinePlayer off = Bukkit.getOfflinePlayer(targetName);
        plugin.getPunishManager().tempBan(off, reason, sender.getName(), millis, true);
        return true;
    }
}
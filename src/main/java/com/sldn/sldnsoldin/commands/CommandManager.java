package com.sldn.sldnsoldin.commands;

import com.sldn.sldnsoldin.SLDNSoldin;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class CommandManager implements CommandExecutor {
    private final SLDNSoldin plugin;
    public CommandManager(SLDNSoldin plugin) { this.plugin = plugin; }

    private void logAction(String text) {
        try (FileWriter writer = new FileWriter(plugin.getLogManager().getLogFile(), true)) {
            writer.write("[" + new Date() + "] " + text + "\n");
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка записи в logs.txt!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();

        if (cmd.equals("sldn")) {
            sender.sendMessage(ChatColor.GREEN + "Доступные команды: /ban /unban /tempban /sldn");
            return true;
        }
        if (cmd.equals("ban")) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Использование: /ban <ник> [причина]");
                return true;
            }
            String playerName = args[0];
            String reason = args.length > 1 ? String.join(" ", args).substring(playerName.length()).trim() : "Нарушение правил";
            Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, reason, null, sender.getName());
            Player target = Bukkit.getPlayerExact(playerName);
            if (target != null) target.kickPlayer("Вы были забанены: " + reason);
            sender.sendMessage(ChatColor.RED + "Игрок " + playerName + " забанен. Причина: " + reason);
            logAction(sender.getName() + " забанил " + playerName + " причина: " + reason);
            return true;
        }
        if (cmd.equals("unban")) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Использование: /unban <ник>");
                return true;
            }
            String playerName = args[0];
            Bukkit.getBanList(BanList.Type.NAME).pardon(playerName);
            sender.sendMessage(ChatColor.GREEN + "Игрок " + playerName + " разбанен.");
            logAction(sender.getName() + " разбанил " + playerName);
            return true;
        }
        if (cmd.equals("tempban")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Использование: /tempban <ник> <секунды> [причина]");
                return true;
            }
            String playerName = args[0];
            long seconds;
            try { seconds = Long.parseLong(args[1]); }
            catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Неверное число секунд.");
                return true;
            }
            String reason = args.length > 2 ? String.join(" ", args).substring(playerName.length() + args[1].length() + 2) : "Нарушение правил";
            Date expire = new Date(System.currentTimeMillis() + seconds * 1000);
            Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, reason, expire, sender.getName());
            Player target = Bukkit.getPlayerExact(playerName);
            if (target != null) target.kickPlayer("Вы временно забанены на " + seconds + " сек. Причина: " + reason);
            sender.sendMessage(ChatColor.RED + "Игрок " + playerName + " забанен на " + seconds + " сек. Причина: " + reason);
            logAction(sender.getName() + " временно забанил " + playerName + " на " + seconds + " сек. Причина: " + reason);
            return true;
        }
        return false;
    }
}

package com.sldn.sldnsoldin.commands;

import com.sldn.sldnsoldin.SldnMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SldnCommand implements CommandExecutor {
    private final SldnMain plugin;
    public SldnCommand(SldnMain plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sldn.admin")) {
            sender.sendMessage(ChatColor.RED + "Нет прав.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GRAY + "/sldn test <ник> - тест-казнь (1 сек)");
            sender.sendMessage(ChatColor.GRAY + "/sldn ban <ник> - пермабан с казнью");
            sender.sendMessage(ChatColor.GRAY + "/sldn banned - список забаненных античитом");
            sender.sendMessage(ChatColor.GRAY + "/sldn gui - панель античита");
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "test": {
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Использование: /sldn test <ник>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Игрок оффлайн."); return true; }
                plugin.punish().startExecution(target, 1, false);
                sender.sendMessage(ChatColor.GREEN + "Тест-казнь запущена для " + target.getName());
                return true;
            }
            case "ban": {
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Использование: /sldn ban <ник>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Игрок оффлайн."); return true; }
                int seconds = plugin.getConfig().getInt("settings.execution-seconds", 20);
                plugin.punish().startExecution(target, seconds, true);
                sender.sendMessage(ChatColor.GREEN + "Казнь/бан запущены для " + target.getName());
                return true;
            }
            case "banned": {
                plugin.punish().sendBannedList(sender);
                return true;
            }
            case "gui": {
                if (sender instanceof Player) plugin.gui().open((Player) sender);
                else sender.sendMessage("Только в игре.");
                return true;
            }
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная подкоманда.");
                return true;
        }
    }
}

package com.sldn.sldnsoldin.commands;

import com.sldn.sldnsoldin.SLDNSoldin;
import org.bukkit.Bukkit;
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
        if (args.length == 0) {
            sender.sendMessage("§cИспользование: /ban <игрок>, /unban <игрок>, /tempban <игрок> <минуты>");
            return true;
        }

        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "ban":
                if (args.length < 1) {
                    sender.sendMessage("§cИспользование: /ban <игрок>");
                    return true;
                }
                Player targetBan = Bukkit.getPlayer(args[0]);
                if (targetBan != null) {
                    targetBan.kickPlayer("§cВы были забанены!");
                    Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(targetBan.getName(), "Забанен админом", null, sender.getName());
                    sender.sendMessage("§aИгрок " + targetBan.getName() + " забанен.");
                    plugin.getLogManager().log("[BAN] " + targetBan.getName() + " был забанен " + sender.getName());
                } else {
                    sender.sendMessage("§cИгрок не найден.");
                }
                break;

            case "unban":
                if (args.length < 1) {
                    sender.sendMessage("§cИспользование: /unban <игрок>");
                    return true;
                }
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(args[0]);
                sender.sendMessage("§aИгрок " + args[0] + " разбанен.");
                plugin.getLogManager().log("[UNBAN] " + args[0] + " был разбанен " + sender.getName());
                break;

            case "tempban":
                if (args.length < 2) {
                    sender.sendMessage("§cИспользование: /tempban <игрок> <минуты>");
                    return true;
                }
                Player targetTemp = Bukkit.getPlayer(args[0]);
                try {
                    int minutes = Integer.parseInt(args[1]);
                    long millis = System.currentTimeMillis() + (minutes * 60 * 1000L);
                    if (targetTemp != null) {
                        targetTemp.kickPlayer("§cВы были временно забанены на " + minutes + " минут.");
                        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(targetTemp.getName(), "Временный бан", new java.util.Date(millis), sender.getName());
                        sender.sendMessage("§aИгрок " + targetTemp.getName() + " забанен на " + minutes + " минут.");
                        plugin.getLogManager().log("[TEMPBAN] " + targetTemp.getName() + " был забанен на " + minutes + " минут " + sender.getName());
                    } else {
                        sender.sendMessage("§cИгрок не найден.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cУкажите правильное число минут.");
                }
                break;

            default:
                sender.sendMessage("§cНеизвестная команда.");
        }
        return true;
    }
}

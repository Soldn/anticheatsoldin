package com.sldn.sldnsoldin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickVeypCommand implements CommandExecutor {
    private final SLDNSoldin plugin;

    public KickVeypCommand(SLDNSoldin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§cИспользование: /kickveyp <игрок>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден.");
            return true;
        }

        target.kickPlayer("Вы были кикнуты системой античита.");
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(target.getName(), "Подозрение на читы", null, sender.getName());
        sender.sendMessage("§aИгрок " + target.getName() + " был кикнут и забанен.");
        return true;
    }
}

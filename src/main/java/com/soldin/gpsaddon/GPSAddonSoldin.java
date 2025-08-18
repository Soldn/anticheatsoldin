package com.soldin.gpsaddon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GPSAddonSoldin extends JavaPlugin implements TabExecutor {

    private boolean enabled = true; // включено по умолчанию
    private int taskId = -1;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        startTask();
        getCommand("sgps").setExecutor(this);
        getLogger().info("GPSAddonSoldin включён!");
    }

    @Override
    public void onDisable() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        getLogger().info("GPSAddonSoldin выключен!");
    }

    private void startTask() {
        int interval = getConfig().getInt("update-interval", 20);
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (!enabled) return;

            String format = getConfig().getString("actionbar-format", "XYZ: {x} {y} {z} | HP: {hp} | FOOD: {food}");
            for (Player player : Bukkit.getOnlinePlayers()) {
                String msg = format
                        .replace("{x}", String.valueOf(player.getLocation().getBlockX()))
                        .replace("{y}", String.valueOf(player.getLocation().getBlockY()))
                        .replace("{z}", String.valueOf(player.getLocation().getBlockZ()))
                        .replace("{hp}", String.valueOf((int) player.getHealth()))
                        .replace("{food}", String.valueOf(player.getFoodLevel()));
                player.sendActionBar(ChatColor.YELLOW + msg);
            }
        }, 20L, interval);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Используй: /sgps <reload|on|off>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reloadConfig();
                startTask();
                sender.sendMessage(ChatColor.GREEN + "GPSAddon конфиг перезагружен!");
                break;
            case "on":
                enabled = true;
                sender.sendMessage(ChatColor.GREEN + "GPSAddon включён!");
                break;
            case "off":
                enabled = false;
                sender.sendMessage(ChatColor.RED + "GPSAddon выключен!");
                break;
            default:
                sender.sendMessage(ChatColor.YELLOW + "Используй: /sgps <reload|on|off>");
        }
        return true;
    }
}

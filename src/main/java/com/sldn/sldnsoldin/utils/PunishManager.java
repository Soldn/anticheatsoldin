package com.sldn.sldnsoldin.utils;

import com.sldn.sldnsoldin.SLDNSoldin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishManager {

    private final SLDNSoldin plugin;

    public PunishManager(SLDNSoldin plugin) {
        this.plugin = plugin;
    }

    public void ban(Player player, String reason) {
        plugin.getLogManager().log("Player " + player.getName() + " banned for: " + reason);
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.kickPlayer("Banned: " + reason);
        });
    }

    public void tempBan(Player player, String reason, long durationMs) {
        plugin.getLogManager().log("Player " + player.getName() + " temp-banned for " + durationMs + "ms: " + reason);
        // TODO: save tempban info in file/database
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.kickPlayer("TempBan: " + reason);
        });
    }

    public void unban(String playerName) {
        plugin.getLogManager().log("Player " + playerName + " unbanned");
        // TODO: remove ban info
    }
}

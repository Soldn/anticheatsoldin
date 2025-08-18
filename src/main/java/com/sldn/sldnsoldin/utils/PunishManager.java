package com.sldn.sldnsoldin.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishManager {
    private final LogManager logManager;

    public PunishManager(LogManager logManager) {
        this.logManager = logManager;
    }

    // старый метод
    public void flag(Player player, String reason) {
        logManager.log(player.getName() + " flagged: " + reason);
    }

    // новый метод (расширенный)
    public void flag(Player player, String check, String reason, boolean autoban) {
        String msg = player.getName() + " flagged (" + check + "): " + reason;
        logManager.log(player.getName(), check, reason);

        if (autoban) {
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("SLDNSoldin"), () -> {
                player.kickPlayer("§cВы были забанены античитом (§4" + check + "§c)");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "ban " + player.getName() + " [SLDNSoldin] Читы: " + check);
            });
        }
    }
}

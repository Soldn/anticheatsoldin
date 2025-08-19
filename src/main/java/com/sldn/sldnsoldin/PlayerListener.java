package com.sldn.sldnsoldin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;

public class PlayerListener implements Listener {
    private final SLDNSoldin plugin;

    public PlayerListener(SLDNSoldin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().distance(event.getTo()) > 10) { // простая проверка на телепорт/флай
            player.kickPlayer("Подозрение на использование читов (слишком быстрое перемещение)");
        }
    }
}

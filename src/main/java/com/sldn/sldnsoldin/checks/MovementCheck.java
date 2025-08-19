package com.sldn.sldnsoldin.checks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;

public class MovementCheck implements Listener {

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.isGliding()) {
                event.setCancelled(true);
                player.sendMessage("§cГлайд (элитры) запрещён античитом!");
            }
        }
    }
}

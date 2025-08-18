package org.sldn.soldin.checks;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyCheck extends Check implements Listener {

    private final Map<UUID, Integer> airTicks = new HashMap<>();

    public FlyCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getAllowFlight() || p.getGameMode() == GameMode.CREATIVE || p.isInsideVehicle()) return;

        Location loc = p.getLocation().clone();
        boolean nearGround = false;
        for (int y = -1; y >= -2; y--) {
            if (loc.clone().add(0, y, 0).getBlock().getType().isSolid()) { nearGround = true; break; }
        }

        if (!nearGround && p.getVelocity().getY() >= -0.05) {
            int t = airTicks.getOrDefault(p.getUniqueId(), 0) + 1;
            airTicks.put(p.getUniqueId(), t);
            if (t > 30) {
                plugin.addViolation(p, "Fly", 2);
            }
        } else {
            airTicks.put(p.getUniqueId(), 0);
        }
    }

    @Override
    public int getVL(Player p) { return airTicks.getOrDefault(p.getUniqueId(), 0) / 10; }
}

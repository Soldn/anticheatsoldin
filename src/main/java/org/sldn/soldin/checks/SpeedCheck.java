package org.sldn.soldin.checks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedCheck extends Check implements Listener {

    private final Map<UUID, Integer> vl = new HashMap<>();

    public SpeedCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.isFlying() || p.isInsideVehicle()) return;

        Vector from = e.getFrom().toVector();
        Vector to = e.getTo().toVector();
        double horizontal = Math.hypot(to.getX() - from.getX(), to.getZ() - from.getZ());

        double limit = 0.42; // safe baseline
        if (p.isSprinting()) limit += 0.1;
        if (p.getWalkSpeed() > 0.2f) limit += (p.getWalkSpeed() - 0.2f) * 1.5;
        if (horizontal > limit) {
            int v = vl.getOrDefault(p.getUniqueId(), 0) + 1;
            vl.put(p.getUniqueId(), v);
            if (v >= plugin.cfg().getInt("violations.speed", 6)) {
                plugin.addViolation(p, "Speed", 2);
            }
        } else {
            vl.put(p.getUniqueId(), Math.max(0, vl.getOrDefault(p.getUniqueId(), 0) - 1));
        }
    }

    @Override
    public int getVL(Player p) { return vl.getOrDefault(p.getUniqueId(), 0); }
}

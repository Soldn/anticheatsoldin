package org.sldn.soldin.checks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraCheck extends Check implements Listener {

    private final Map<UUID, Integer> aimVL = new HashMap<>();
    private final Map<UUID, Long> lastHit = new HashMap<>();

    public KillAuraCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private double angleBetween(Player p, Entity t) {
        Vector eyeDir = p.getEyeLocation().getDirection().normalize();
        Vector toTarget = t.getLocation().add(0, t.getHeight() * 0.5, 0).toVector().subtract(p.getEyeLocation().toVector()).normalize();
        double dot = eyeDir.dot(toTarget);
        dot = Math.max(-1.0, Math.min(1.0, dot));
        return Math.toDegrees(Math.acos(dot));
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        Entity t = e.getEntity();
        if (!(t instanceof LivingEntity)) return;

        // line of sight check
        if (!p.hasLineOfSight(t)) {
            int v = aimVL.getOrDefault(p.getUniqueId(), 0) + 1;
            aimVL.put(p.getUniqueId(), v);
        } else {
            // angle check
            double angle = angleBetween(p, t);
            if (angle > 55.0) {
                int v = aimVL.getOrDefault(p.getUniqueId(), 0) + 1;
                aimVL.put(p.getUniqueId(), v);
            } else {
                aimVL.put(p.getUniqueId(), Math.max(0, aimVL.getOrDefault(p.getUniqueId(), 0) - 1));
            }
        }

        int vl = aimVL.getOrDefault(p.getUniqueId(), 0);
        if (vl >= plugin.cfg().getInt("violations.killaura", 6)) {
            plugin.addViolation(p, "KillAura(Aim)", 1);
        }

        // CPS moved to AutoClickerCheck; only minor contribution here
        lastHit.put(p.getUniqueId(), System.currentTimeMillis());
    }

    @Override
    public int getVL(Player p) { return aimVL.getOrDefault(p.getUniqueId(), 0); }
}

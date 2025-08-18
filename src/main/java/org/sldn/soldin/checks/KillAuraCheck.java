package org.sldn.soldin.checks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.sldn.soldin.SldnSoldin;

import java.util.*;

public class KillAuraCheck extends Check implements Listener {

    private final Map<UUID, Long> lastHit = new HashMap<>();
    private final Map<UUID, Integer> cpsVL = new HashMap<>();
    private final Map<UUID, UUID> lastTarget = new HashMap<>();
    private final Map<UUID, Integer> switchVL = new HashMap<>();

    public KillAuraCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        Entity t = e.getEntity();

        long now = System.currentTimeMillis();
        long last = lastHit.getOrDefault(p.getUniqueId(), 0L);
        long delta = now - last;
        lastHit.put(p.getUniqueId(), now);

        // CPS/APS heuristic
        if (delta < 60) { // >16 CPS
            int v = cpsVL.getOrDefault(p.getUniqueId(), 0) + 2;
            cpsVL.put(p.getUniqueId(), v);
        } else if (delta < 90) {
            int v = cpsVL.getOrDefault(p.getUniqueId(), 0) + 1;
            cpsVL.put(p.getUniqueId(), v);
        } else {
            cpsVL.put(p.getUniqueId(), Math.max(0, cpsVL.getOrDefault(p.getUniqueId(), 0) - 1));
        }

        // Target switch rate
        UUID lt = lastTarget.get(p.getUniqueId());
        if (lt != null && !lt.equals(t.getUniqueId()) && delta < 120) {
            int v = switchVL.getOrDefault(p.getUniqueId(), 0) + 1;
            switchVL.put(p.getUniqueId(), v);
        } else {
            switchVL.put(p.getUniqueId(), Math.max(0, switchVL.getOrDefault(p.getUniqueId(), 0) - 1));
        }
        lastTarget.put(p.getUniqueId(), t.getUniqueId());

        int total = cpsVL.getOrDefault(p.getUniqueId(), 0) + switchVL.getOrDefault(p.getUniqueId(), 0);
        if (total >= plugin.cfg().getInt("violations.killaura", 6)) {
            plugin.addViolation(p, "KillAura", 3);
        }
    }

    @Override
    public int getVL(Player p) {
        return cpsVL.getOrDefault(p.getUniqueId(), 0) + switchVL.getOrDefault(p.getUniqueId(), 0);
    }
}

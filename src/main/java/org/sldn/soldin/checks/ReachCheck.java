package org.sldn.soldin.checks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReachCheck extends Check implements Listener {

    private final Map<UUID, Integer> vl = new HashMap<>();

    public ReachCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        Entity t = e.getEntity();

        double dist = p.getLocation().toVector().setY(0).distance(t.getLocation().toVector().setY(0));
        double limit = 3.2; // conservative
        if (dist > limit) {
            int v = vl.getOrDefault(p.getUniqueId(), 0) + 1;
            vl.put(p.getUniqueId(), v);
            if (v >=  plugin.cfg().getInt("violations.reach", 5)) {
                plugin.addViolation(p, "Reach (" + String.format("%.2f", dist) + ")", 2);
            }
        } else {
            vl.put(p.getUniqueId(), Math.max(0, vl.getOrDefault(p.getUniqueId(), 0) - 1));
        }
    }

    @Override
    public int getVL(Player p) { return vl.getOrDefault(p.getUniqueId(), 0); }
}

package org.sldn.soldin.checks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoFallCheck extends Check implements Listener {

    private final Map<UUID, Double> maxFallDist = new HashMap<>();

    public NoFallCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double dy = e.getTo().getY() - e.getFrom().getY();
        if (dy < 0) { // falling
            double accum = maxFallDist.getOrDefault(p.getUniqueId(), 0.0) - dy;
            maxFallDist.put(p.getUniqueId(), accum);
        } else if (dy > 0) {
            maxFallDist.put(p.getUniqueId(), 0.0);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        Player p = (Player) e.getEntity();
        // if we recorded long fall but there was no damage (event cancelled elsewhere), we can't see here.
        // But we can use damage scaling vs distance.
        double tracked = maxFallDist.getOrDefault(p.getUniqueId(), 0.0);
        if (tracked > 3.5 && e.getFinalDamage() <= 0.0) {
            plugin.addViolation(p, "NoFall", 2);
        }
        maxFallDist.put(p.getUniqueId(), 0.0);
    }

    @Override
    public int getVL(Player p) {
        return (int)Math.floor(maxFallDist.getOrDefault(p.getUniqueId(), 0.0) / 10.0);
    }
}

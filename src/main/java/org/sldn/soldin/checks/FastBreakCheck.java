package org.sldn.soldin.checks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastBreakCheck extends Check implements Listener {

    private final Map<UUID, Long> lastBreak = new HashMap<>();
    private final Map<UUID, Integer> vl = new HashMap<>();

    public FastBreakCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        long now = System.currentTimeMillis();
        long last = lastBreak.getOrDefault(p.getUniqueId(), 0L);
        long delta = now - last;
        lastBreak.put(p.getUniqueId(), now);

        // simple heuristic: some blocks require base time; if too fast repeatedly -> flag
        if (delta < 80) { // ~12.5 blocks/sec unrealistic without haste + insta-mine
            int v = vl.getOrDefault(p.getUniqueId(), 0) + 1;
            vl.put(p.getUniqueId(), v);
            if (v >= plugin.cfg().getInt("violations.fastbreak", 8)) {
                plugin.addViolation(p, "FastBreak", 2);
            }
        } else {
            vl.put(p.getUniqueId(), Math.max(0, vl.getOrDefault(p.getUniqueId(), 0) - 1));
        }
    }

    @Override
    public int getVL(Player p) { return vl.getOrDefault(p.getUniqueId(), 0); }
}

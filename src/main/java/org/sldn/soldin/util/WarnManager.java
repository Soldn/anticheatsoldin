package org.sldn.soldin.util;

import org.bukkit.entity.Player;
import org.sldn.soldin.SldnSoldin;

import java.util.*;

public class WarnManager {
    private final SldnSoldin plugin;
    private final Map<UUID, Integer> streak = new HashMap<>();
    private final Map<UUID, Long> lastTime = new HashMap<>();

    public WarnManager(SldnSoldin plugin) { this.plugin = plugin; }

    public int addWarn(Player p) {
        int resetSec = plugin.cfg().getInt("settings.warn-reset-seconds", 60);
        long now = System.currentTimeMillis();
        long last = lastTime.getOrDefault(p.getUniqueId(), 0L);
        if (now - last > resetSec * 1000L) {
            streak.put(p.getUniqueId(), 0);
        }
        int s = streak.getOrDefault(p.getUniqueId(), 0) + 1;
        streak.put(p.getUniqueId(), s);
        lastTime.put(p.getUniqueId(), now);
        return s;
    }

    public void reset(Player p) {
        streak.remove(p.getUniqueId());
        lastTime.remove(p.getUniqueId());
    }

    public int get(Player p) { return streak.getOrDefault(p.getUniqueId(), 0); }
}

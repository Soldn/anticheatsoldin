
package com.sldn.sldnsoldin.util;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatusTracker {
    public static class Status {
        public long lastExplosionMs = 0L;
        public long lastVelocityMs = 0L;
        public long lastGlideMs = 0L;
        public long lastLevitationMs = 0L;

        public long lastCpsWindowStart = 0L;
        public int clicksInWindow = 0;

        public long lastHitTargetsWindowStart = 0L;
        public int distinctTargets = 0;
    }

    private final Map<UUID, Status> map = new ConcurrentHashMap<>();

    public Status of(Player p) {
        return map.computeIfAbsent(p.getUniqueId(), u -> new Status());
    }
}

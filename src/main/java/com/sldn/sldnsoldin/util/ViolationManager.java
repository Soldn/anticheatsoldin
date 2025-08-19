
package com.sldn.sldnsoldin.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ViolationManager {

    public static class Counters {
        private final Map<String, Integer> map = new HashMap<>();

        public int addAndGet(String type, int delta) {
            int v = map.getOrDefault(type, 0) + delta;
            if (v < 0) v = 0;
            map.put(type, v);
            return v;
        }

        public int get(String type) {
            return map.getOrDefault(type, 0);
        }

        public void reset(String type) {
            map.remove(type);
        }
    }

    private final ConcurrentHashMap<String, Counters> playerMap = new ConcurrentHashMap<>();

    public Counters of(String player) {
        return playerMap.computeIfAbsent(player, k -> new Counters());
    }
}

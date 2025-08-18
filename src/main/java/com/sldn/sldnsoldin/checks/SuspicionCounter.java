package com.sldn.sldnsoldin.checks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class SuspicionCounter {
    private static final Map<String, Integer> COUNTS = new HashMap<>();

    static int inc(UUID uuid, String key) {
        String k = uuid.toString() + ":" + key;
        int v = COUNTS.getOrDefault(k, 0) + 1;
        COUNTS.put(k, v);
        return v;
    }

    static void reset(UUID uuid, String key) {
        String k = uuid.toString() + ":" + key;
        COUNTS.remove(k);
    }
}
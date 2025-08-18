package com.sldn.sldnsoldin.utils;

public class Text {
    public static long parseDurationMillis(String s) {
        try {
            s = s.trim().toLowerCase();
            long mult = 1000L;
            if (s.endsWith("ms")) { mult = 1L; s = s.substring(0, s.length()-2); }
            else if (s.endsWith("s")) { mult = 1000L; s = s.substring(0, s.length()-1); }
            else if (s.endsWith("m")) { mult = 60_000L; s = s.substring(0, s.length()-1); }
            else if (s.endsWith("h")) { mult = 3_600_000L; s = s.substring(0, s.length()-1); }
            else if (s.endsWith("d")) { mult = 86_400_000L; s = s.substring(0, s.length()-1); }
            long val = Long.parseLong(s);
            return val * mult;
        } catch (Exception e) {
            return -1L;
        }
    }
}
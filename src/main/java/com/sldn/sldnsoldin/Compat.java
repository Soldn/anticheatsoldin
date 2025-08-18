package com.sldn.sldnsoldin;

import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

public class Compat {

    public static PotionEffectType getSlowness() {
        return PotionEffectType.SLOW != null ? PotionEffectType.SLOW : PotionEffectType.SLOW_DIGGING;
    }

    public static Particle getSmoke() {
        try {
            return Particle.SMOKE_LARGE;
        } catch (NoSuchFieldError e) {
            return Particle.SMOKE_NORMAL;
        }
    }
}

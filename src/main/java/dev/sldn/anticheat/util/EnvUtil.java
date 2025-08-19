package dev.sldn.anticheat.util;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnvUtil {
    public static int speedLevel(Player p){
        PotionEffect e = p.getPotionEffect(PotionEffectType.SPEED);
        return e==null?0:e.getAmplifier()+1;
    }
    public static int soulSpeedLevel(Player p){
        if (p.getInventory().getBoots()==null) return 0;
        return p.getInventory().getBoots().getEnchantmentLevel(org.bukkit.enchantments.Enchantment.SOUL_SPEED);
    }
    public static boolean hasDolphinsGrace(Player p){ return p.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE); }
}

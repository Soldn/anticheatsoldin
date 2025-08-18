package com.sldn.sldnsoldin.logic;

import com.sldn.sldnsoldin.Compat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Bukkit;
import org.bukkit.Particle;

public class PunishManager {

    public static void punish(Player player) {
        PotionEffectType slow = Compat.getSlowness();
        player.addPotionEffect(new PotionEffect(slow, 200, 1));

        player.getWorld().spawnParticle(Compat.getSmoke(), player.getLocation(), 50);
        Bukkit.getLogger().info("Punished player: " + player.getName());
    }
}

package com.sldn.sldnsoldin.logic;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PunishManager {

    // Универсальный метод для Slow
    private static PotionEffectType getSlowEffect() {
        try {
            return PotionEffectType.valueOf("SLOW"); // 1.16–1.20
        } catch (IllegalArgumentException e) {
            return PotionEffectType.valueOf("SLOWNESS"); // 1.21+
        }
    }

    // Универсальный метод для Smoke
    private static Particle getSmokeParticle() {
        try {
            return Particle.valueOf("SMOKE_LARGE"); // 1.16–1.20
        } catch (IllegalArgumentException e) {
            return Particle.valueOf("LARGE_SMOKE"); // 1.21+
        }
    }

    // Пример наказания (бан + эффект + партиклы)
    public static void punishPlayer(Player player, String reason) {
        // Даем эффект замедления
        player.addPotionEffect(new PotionEffect(getSlowEffect(), 200, 2));

        // Спавним партиклы
        player.getWorld().spawnParticle(getSmokeParticle(),
                player.getLocation(), 50, 0.5, 0.5, 0.5, 0.01);

        // Бан игрока
        Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("sldnsoldin"),
                () -> player.kickPlayer("§cВы были забанены! Причина: " + reason),
                40L
        );

        Bukkit.getLogger().info("[SLDN] Игрок " + player.getName() + " был забанен. Причина: " + reason);
    }
}

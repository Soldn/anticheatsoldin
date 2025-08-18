package com.sldn.sldnsoldin.logic;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class PunishManager {

    private final HashMap<UUID, Long> bannedPlayers = new HashMap<>();

    public void tempBan(Player player, String reason, int seconds) {
        UUID uuid = player.getUniqueId();
        bannedPlayers.put(uuid, System.currentTimeMillis() + (seconds * 1000L));

        player.kickPlayer("§cВы временно забанены!\n§7Причина: " + reason + "\n§7Время: " + seconds + " сек.");

        Bukkit.broadcastMessage("§cИгрок " + player.getName() + " получил временный бан! Причина: " + reason);
    }

    public void ban(Player player, String reason) {
        UUID uuid = player.getUniqueId();
        bannedPlayers.put(uuid, -1L); // -1 = перманентный бан

        player.kickPlayer("§cВы забанены навсегда!\n§7Причина: " + reason);

        Bukkit.broadcastMessage("§cИгрок " + player.getName() + " был забанен! Причина: " + reason);
    }

    public void kick(Player player, String reason) {
        player.kickPlayer("§cВас кикнул античит!\n§7Причина: " + reason);
        Bukkit.broadcastMessage("§eИгрок " + player.getName() + " был кикнут. Причина: " + reason);
    }

    public boolean isBanned(Player player) {
        UUID uuid = player.getUniqueId();
        if (!bannedPlayers.containsKey(uuid)) return false;

        long expire = bannedPlayers.get(uuid);
        if (expire == -1L) return true; // Перманентный бан
        if (System.currentTimeMillis() > expire) {
            bannedPlayers.remove(uuid); // Снятие бана после истечения времени
            return false;
        }
        return true;
    }

    public void punishAnimation(Player player) {
        // Эффекты при наказании
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));
        player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 40, 1, 1, 1, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
    }
}

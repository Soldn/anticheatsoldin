package com.sldn.sldnsoldin.logic;

import com.sldn.sldnsoldin.SLDNSoldin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class PunishManager {

    private static void log(String player, String reason, boolean ban) {
        try {
            FileWriter writer = new FileWriter(SLDNSoldin.getInstance().getDataFolder() + "/logs.txt", true);
            writer.write(new Date() + " | " + player + " | " + reason + (ban ? " [BAN]" : "") + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void playPunishAnimation(Player player) {
        player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
    }

    public static void ban(Player player, String reason) {
        playPunishAnimation(player);
        log(player.getName(), reason, true);
        Bukkit.getScheduler().runTaskLater(SLDNSoldin.getInstance(), () -> {
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(
                    player.getName(), "Забанен античитом: " + reason, null, "SLDNSoldin");
            player.kick(ChatColor.RED + "Ты был забанен античитом!\nПричина: " + reason);
        }, 40L);
    }

    public static void tempBan(Player player, String reason, int duration, String unit) {
        long millis = System.currentTimeMillis();
        long expire;
        switch (unit.toLowerCase()) {
            case "s": expire = millis + (duration * 1000L); break;
            case "m": expire = millis + (duration * 60_000L); break;
            case "h": expire = millis + (duration * 3_600_000L); break;
            case "d": expire = millis + (duration * 86_400_000L); break;
            default: expire = millis + (duration * 60_000L);
        }
        Date expireDate = new Date(expire);

        playPunishAnimation(player);
        log(player.getName(), reason + " (до " + expireDate + ")", true);

        Bukkit.getScheduler().runTaskLater(SLDNSoldin.getInstance(), () -> {
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(
                    player.getName(),
                    "Временный бан до " + expireDate + " | Причина: " + reason,
                    expireDate,
                    "SLDNSoldin"
            );
            player.kick(ChatColor.RED + "Ты был временно забанен античитом!\nДо: " + expireDate + "\nПричина: " + reason);
        }, 40L);
    }

    public static void kick(Player player, String reason) {
        playPunishAnimation(player);
        log(player.getName(), "Кик: " + reason, false);
        Bukkit.getScheduler().runTaskLater(SLDNSoldin.getInstance(), () -> {
            player.kick(ChatColor.RED + "Ты был кикнут!\nПричина: " + reason);
        }, 40L);
    }
}

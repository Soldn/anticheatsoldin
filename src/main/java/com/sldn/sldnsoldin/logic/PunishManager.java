package com.sldn.sldnsoldin.logic;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;

public class PunishManager {

    // Простая "анимация": молния без урона + лёгкий подброс
    public static void playPunishAnimation(Player player) {
        player.getWorld().strikeLightningEffect(player.getLocation());
        player.setVelocity(player.getVelocity().setY(1.0));
    }

    public static void ban(Player player, String reason) {
        playPunishAnimation(player);
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                .addBan(player.getName(), reason, null, "SLDNSoldin");
        player.kickPlayer("Вы забанены: " + reason);
        Bukkit.broadcastMessage(ChatColor.RED + "[BAN] " + ChatColor.WHITE
                + player.getName() + " → " + reason);
    }

    public static void tempBan(Player player, String reason, int minutes) {
        playPunishAnimation(player);
        Date expires = new Date(System.currentTimeMillis() + minutes * 60L * 1000L);
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME)
                .addBan(player.getName(), reason, expires, "SLDNSoldin");
        player.kickPlayer("Временный бан на " + minutes + " мин. Причина: " + reason);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[TEMPBAN] " + ChatColor.WHITE
                + player.getName() + " → " + reason + " (" + minutes + "m)");
    }

    public static void kick(Player player, String reason) {
        playPunishAnimation(player);
        player.kickPlayer("Кик: " + reason);
        Bukkit.broadcastMessage(ChatColor.GOLD + "[KICK] " + ChatColor.WHITE
                + player.getName() + " → " + reason);
    }
}

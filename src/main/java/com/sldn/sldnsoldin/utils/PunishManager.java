package com.sldn.sldnsoldin.utils;

import com.sldn.sldnsoldin.SLDNSoldin;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class PunishManager {

    private final SLDNSoldin plugin;

    public PunishManager(SLDNSoldin plugin) {
        this.plugin = plugin;
    }

    public void flag(Player p, String type, String details, boolean strong) {
        String msg = ChatColor.YELLOW + "[FLAG] " + p.getName() + ChatColor.GRAY + " -> " + type + " | " + details;
        Bukkit.broadcast(msg, "sldn.admin");
        plugin.getLogManager().log(p.getName(), "FLAG", type + " | " + details);

        if (strong) {
            // сначала временно заблокируем на 30 минут, не пермабаним сразу
            tempBan(p, type + " | " + details, "Auto", 30L * 60L * 1000L, true);
        }
    }

    public void permBan(OfflinePlayer target, String reason, String actor, boolean announce) {
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, null, actor);
        if (target.isOnline()) {
            animateBan(target.getPlayer(), reason);
            target.getPlayer().kickPlayer(reason);
        }
        if (announce) {
            Bukkit.broadcastMessage(ChatColor.RED + "[BAN] " + target.getName() + " забанен навсегда: " + reason);
        }
    }

    public void tempBan(OfflinePlayer target, String reason, String actor, long millis, boolean announce) {
        Date until = new Date(System.currentTimeMillis() + millis);
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), reason, until, actor);
        if (target.isOnline()) {
            animateBan(target.getPlayer(), reason);
            target.getPlayer().kickPlayer(reason + " (до " + until + ")");
        }
        if (announce) {
            Bukkit.broadcastMessage(ChatColor.RED + "[TEMPBAN] " + target.getName() + " забанен до " + until + ": " + reason);
        }
    }

    public void unban(OfflinePlayer target, String actor) {
        BanEntry be = Bukkit.getBanList(BanList.Type.NAME).getBanEntry(target.getName());
        if (be != null) {
            Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());
            Bukkit.broadcastMessage(ChatColor.GREEN + "[UNBAN] " + target.getName() + " разбанен " + actor);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[UNBAN] игрок " + target.getName() + " не был в бане");
        }
    }

    // "Анимация" — простая титульная заставка + звуки
    private void animateBan(Player p, String reason) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!p.isOnline() || ticks > 40) { cancel(); return; }
                p.sendTitle(ChatColor.DARK_RED + "B A N", ChatColor.RED + reason, 0, 10, 0);
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
                ticks += 10;
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
}
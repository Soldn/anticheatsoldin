package dev.sldn.anticheat.punish;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.api.CheckType;
import dev.sldn.anticheat.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PunishmentManager implements Listener {
    private final SldnAntiCheat plugin;
    private final File logDir;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final Map<UUID, Long> tempBans = new HashMap<>();

    public PunishmentManager(SldnAntiCheat plugin) {
        this.plugin = plugin;
        logDir = new File(plugin.getDataFolder(), plugin.getConfig().getString("logs.folder","logs"));
        if (!logDir.exists()) logDir.mkdirs();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void consider(Player p, CheckType type, String reason, int totalVL) {
        int threshold = plugin.getConfig().getInt("checks.autobanThreshold", 25);
        if (totalVL < threshold) return;

        String mode = plugin.getConfig().getString("checks.punishMode","ban");
        switch (mode.toLowerCase()) {
            case "kick" -> {
                p.kick(org.bukkit.ChatColor.RED + "[SLDN] Чит: " + type.name());
                Msg.punish(p, "KICK", type, totalVL);
            }
            case "tempban" -> {
                long minutes = plugin.getConfig().getLong("checks.tempBanMinutes",60);
                long until = System.currentTimeMillis() + minutes*60*1000L;
                tempBans.put(p.getUniqueId(), until);
                p.kick(org.bukkit.ChatColor.RED + "[SLDN] Временный бан на " + minutes + " минут. Чит: " + type.name());
                Msg.punish(p, "TEMPBAN", type, totalVL);
            }
            case "ban" -> {
                String cmd = plugin.getConfig().getString("checks.banCommand")
                        .replace("%player%", p.getName())
                        .replace("%reason%", type.name())
                        .replace("%vl%", String.valueOf(totalVL));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                Msg.punish(p, "BAN", type, totalVL);
            }
        }
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        UUID id = e.getUniqueId();
        if (tempBans.containsKey(id)) {
            long until = tempBans.get(id);
            if (System.currentTimeMillis() < until) {
                long left = (until - System.currentTimeMillis())/1000;
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "§c[SLDN] Временный бан. Осталось: "+left+" сек.");
            } else tempBans.remove(id);
        }
    }

    public void log(Player p, CheckType type, String reason, int vl) {
        if (!plugin.getConfig().getBoolean("logs.enabled", true)) return;
        String date = sdf.format(new Date());
        String line = String.format("[%tT] %s %s VL=%d REASON=%s%n", new Date(), p.getName(), type.name(), vl, reason);
        write(new File(logDir, "all-"+date+".log"), line);
        String cat = switch(type){
            case KILLAURA -> "combat";
            case AUTOCLICKER -> "autoclicker";
            case FLY, SPEED -> "movement";
        };
        write(new File(logDir, cat+"-"+date+".log"), line);
    }

    private void write(File f, String line){
        try (FileWriter w = new FileWriter(f, true)) { w.write(line);} catch (IOException ignored) {}
    }
}

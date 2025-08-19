
package com.sldn.sldnsoldin.checks;

import com.sldn.sldnsoldin.SLDNSoldin;
import com.sldn.sldnsoldin.util.Logs;
import com.sldn.sldnsoldin.util.StatusTracker;
import com.sldn.sldnsoldin.util.ViolationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleGlideEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class MovementCheck implements Listener {

    private final SLDNSoldin plugin;
    private final Logs logs;
    private final ViolationManager vio;
    private final StatusTracker tracker;

    public MovementCheck(SLDNSoldin plugin, Logs logs, ViolationManager vio, StatusTracker tracker) {
        this.plugin = plugin;
        this.logs = logs;
        this.vio = vio;
        this.tracker = tracker;
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof org.bukkit.entity.TNTPrimed) {
            e.getLocation().getWorld().getNearbyPlayers(e.getLocation(), 8).forEach(p -> {
                tracker.of(p).lastExplosionMs = System.currentTimeMillis();
            });
        }
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent e) {
        tracker.of(e.getPlayer()).lastVelocityMs = System.currentTimeMillis();
    }

    @EventHandler
    public void onToggleGlide(PlayerToggleGlideEvent e) {
        if (e.isGliding()) {
            tracker.of(e.getPlayer()).lastGlideMs = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
                e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                tracker.of(p).lastExplosionMs = System.currentTimeMillis();
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        if (p.isInsideVehicle()) return;
        if (p.getAllowFlight()) return; // у админов/донатов может быть флай
        if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() == Material.ELYTRA) {
            // если реально планер надет — допустим
        }
        long now = System.currentTimeMillis();
        long tntWindow = plugin.getConfig().getLong("fly.tntKnockbackWindowMs", 4000);
        StatusTracker.Status st = tracker.of(p);

        // Исключения
        if (plugin.getConfig().getBoolean("fly.allowElytra", true) && p.isGliding()) {
            st.lastGlideMs = now;
        }
        if (plugin.getConfig().getBoolean("fly.allowLevitation", true) && p.hasPotionEffect(PotionEffectType.LEVITATION)) {
            st.lastLevitationMs = now;
        }

        boolean inImmunity = (now - st.lastExplosionMs) < tntWindow ||
                             (now - st.lastVelocityMs) < tntWindow ||
                             (now - st.lastGlideMs) < tntWindow ||
                             (now - st.lastLevitationMs) < tntWindow;

        Vector from = e.getFrom().toVector();
        Vector to = e.getTo().toVector();
        double dy = to.getY() - from.getY();
        boolean ascending = dy > 0.0;

        // Если на земле — сбрасываем нарушения по флай
        if (p.isOnGround()) {
            vio.of(p.getName()).reset("Fly");
            return;
        }

        // Если иммунитет — не считаем
        if (inImmunity) {
            vio.of(p.getName()).reset("Fly");
            return;
        }

        // Простая эвристика: много тиков подряд в воздухе + заметный подъём
        double vSpeedThreshold = plugin.getConfig().getDouble("fly.verticalSpeedThreshold", 0.9);
        int maxAirTicks = plugin.getConfig().getInt("fly.maxAirTicks", 18);

        // Air ticks можно прикинуть по fallDistance: если долго не растёт при нахождении в воздухе — подозрительно.
        // Здесь используем dy и отсутствие блоков под ногами.
        if (ascending && dy > 0.3 && p.getVelocity().getY() > vSpeedThreshold) {
            int v = vio.of(p.getName()).addAndGet("Fly", 1);
            logs.detection(p.getName(), "Fly", String.format("dy=%.3f vy=%.3f", dy, p.getVelocity().getY()));
            checkBan(p, "Fly", v);
            return;
        }

        // Если висит в воздухе "слишком долго" без падения
        if (!p.isOnGround() && p.getFallDistance() < 0.1f && p.getVelocity().getY() > -0.05) {
            int v = vio.of(p.getName()).addAndGet("Fly", 1);
            logs.detection(p.getName(), "Fly", String.format("airTicks+ hover vy=%.3f", p.getVelocity().getY()));
            checkBan(p, "Fly", v);
        } else {
            // небольшое восстановление при нормальном движении
            vio.of(p.getName()).addAndGet("Fly", -1);
        }
    }

    private void checkBan(Player p, String type, int violations) {
        int threshold = plugin.getConfig().getInt("ban.thresholds." + type, 5);
        if (violations >= threshold) {
            String reason = type + " (" + violations + ")";
            logs.ban(p.getName(), type, reason);
            String cmd = plugin.getConfig().getString("actions.banCommand", "ban %player% [SLDNSoldin] %reason%");
            String finalCmd = cmd.replace("%player%", p.getName()).replace("%reason%", reason);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
        }
    }
}


package com.sldn.sldnsoldin.checks;

import com.sldn.sldnsoldin.SLDNSoldin;
import com.sldn.sldnsoldin.util.Logs;
import com.sldn.sldnsoldin.util.StatusTracker;
import com.sldn.sldnsoldin.util.ViolationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class CombatCheck implements Listener {

    private final SLDNSoldin plugin;
    private final Logs logs;
    private final ViolationManager vio;
    private final StatusTracker tracker;

    public CombatCheck(SLDNSoldin plugin, Logs logs, ViolationManager vio, StatusTracker tracker) {
        this.plugin = plugin;
        this.logs = logs;
        this.vio = vio;
        this.tracker = tracker;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        if (p.getGameMode() == GameMode.SPECTATOR) return;

        if (!(e.getEntity() instanceof LivingEntity)) return;
        LivingEntity target = (LivingEntity) e.getEntity();

        // CPS окно 1 секунда
        long now = System.currentTimeMillis();
        StatusTracker.Status st = tracker.of(p);
        if (now - st.lastCpsWindowStart > 1000) {
            st.lastCpsWindowStart = now;
            st.clicksInWindow = 0;
        }
        st.clicksInWindow++;

        // Мульти-таргет окно
        if (now - st.lastHitTargetsWindowStart > plugin.getConfig().getLong("combat.multiTargetWindowMs", 250)) {
            st.lastHitTargetsWindowStart = now;
            st.distinctTargets = 0;
        }
        st.distinctTargets++;

        double cps = st.clicksInWindow / Math.max(1.0, (now - st.lastCpsWindowStart) / 1000.0);
        double reach = p.getLocation().toVector().distance(target.getLocation().toVector());
        double reachThreshold = plugin.getConfig().getDouble("combat.reachThreshold", 3.2);

        // Угол между взглядом игрока и направлением на цель
        Vector dir = p.getLocation().getDirection().normalize();
        Vector toTarget = target.getLocation().toVector().subtract(p.getLocation().toVector()).normalize();
        double dot = dir.dot(toTarget);
        double angle = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dot))));
        double angleTh = plugin.getConfig().getDouble("combat.angleThresholdDegrees", 75.0);
        int cpsTh = plugin.getConfig().getInt("combat.cpsThreshold", 16);

        boolean suspiciousReach = reach > reachThreshold + 0.15; // небольшой допуск
        boolean suspiciousAim = angle > angleTh && !p.hasLineOfSight(target);
        boolean suspiciousCps = cps > cpsTh;

        // Не баним за один фактор: нужна комбинация
        int add = 0;
        StringBuilder details = new StringBuilder();
        if (suspiciousReach) { add++; details.append(String.format("reach=%.2f>%.2f ", reach, reachThreshold)); }
        if (suspiciousAim)   { add++; details.append(String.format("angle=%.1f>%s ", angle, angleTh)); }
        if (suspiciousCps)   { add++; details.append(String.format("cps=%.1f>%d ", cps, cpsTh)); }
        if (st.distinctTargets >= 3) { add++; details.append("multi-target "); }

        if (add >= 2) {
            int v = vio.of(p.getName()).addAndGet("KillAura", 1);
            logs.detection(p.getName(), "KillAura", details.toString().trim());
            checkBan(p, "KillAura", v);
        } else {
            // восстановление счётчика при нормальных ударах
            vio.of(p.getName()).addAndGet("KillAura", -1);
        }
    }

    private void checkBan(Player p, String type, int violations) {
        int threshold = plugin.getConfig().getInt("ban.thresholds." + type, 6);
        if (violations >= threshold) {
            String reason = type + " (" + violations + ")";
            logs.ban(p.getName(), type, reason);
            String cmd = plugin.getConfig().getString("actions.banCommand", "ban %player% [SLDNSoldin] %reason%");
            String finalCmd = cmd.replace("%player%", p.getName()).replace("%reason%", reason);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
        }
    }
}

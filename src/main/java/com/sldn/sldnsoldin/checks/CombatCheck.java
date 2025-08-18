package com.sldn.sldnsoldin.checks;

import com.sldn.sldnsoldin.SLDNSoldin;
import com.sldn.sldnsoldin.utils.LogManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatCheck implements Listener {

    private final SLDNSoldin plugin;
    private final LogManager logs;

    // Подсчет CPS по анимациям руки
    private final Map<UUID, Integer> clickCount = new HashMap<>();
    private final Map<UUID, Long> windowStart = new HashMap<>();

    public CombatCheck(SLDNSoldin plugin) {
        this.plugin = plugin;
        this.logs = plugin.getLogManager();
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent e) {
        Player p = e.getPlayer();
        long now = System.currentTimeMillis();
        long start = windowStart.getOrDefault(p.getUniqueId(), now);
        if (now - start > 1000L) { // новый секундный интервал
            windowStart.put(p.getUniqueId(), now);
            clickCount.put(p.getUniqueId(), 0);
        }
        clickCount.put(p.getUniqueId(), clickCount.getOrDefault(p.getUniqueId(), 0) + 1);
        int cps = clickCount.get(p.getUniqueId());

        if (cps > 20) { // допуск быстрое кликанье, но не баним, только флаг
            logs.log(p.getName(), "C", "Высокий CPS: " + cps + " (флаг, но не бан)");
            plugin.getPunishManager().flag(p, "Killaura", "Высокий CPS (" + cps + ")", false);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        Entity target = e.getEntity();

        if (!(target instanceof LivingEntity)) return;
        double dist = p.getLocation().distance(target.getLocation());
        if (dist > 5.0) {
            logs.log(p.getName(), "C", "Дальность удара " + String.format("%.2f", dist) + " > 5.0");
            int v = SuspicionCounter.inc(p.getUniqueId(), "reach");
            if (v > 3) {
                plugin.getPunishManager().flag(p, "Killaura/Reach", "Ненормальная дальность (" + dist + ")", true);
            }
        } else {
            SuspicionCounter.reset(p.getUniqueId(), "reach");
        }
    }
}
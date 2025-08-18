package org.sldn.soldin.checks;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.sldn.soldin.SldnSoldin;

import java.util.*;

public class AutoClickerCheck extends Check implements Listener {

    private final Map<UUID, Deque<Long>> clicks = new HashMap<>();
    private final int windowMs = 1000;

    public AutoClickerCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void mark(Player p) {
        Deque<Long> q = clicks.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        q.addLast(now);
        while (!q.isEmpty() && now - q.peekFirst() > windowMs) q.removeFirst();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            mark(e.getPlayer());
            check(e.getPlayer(), null);
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            mark(p);
            check(p, e.getEntity());
        }
    }

    private void check(Player p, Entity target) {
        int cps = clicks.getOrDefault(p.getUniqueId(), new ArrayDeque<>()).size();
        int maxPlayers = plugin.cfg().getInt("anticheat.max-cps-players", 15);
        int maxMobs = plugin.cfg().getInt("anticheat.max-cps-mobs", 20);
        int limit = (target instanceof Player) ? maxPlayers : maxMobs;

        if (cps > limit) {
            plugin.addViolation(p, "AutoClicker(" + cps + " CPS)", 1);
        }
    }

    @Override
    public int getVL(Player p) {
        return Math.max(0, clicks.getOrDefault(p.getUniqueId(), new ArrayDeque<>()).size() - plugin.cfg().getInt("anticheat.max-cps-players", 15));
    }
}

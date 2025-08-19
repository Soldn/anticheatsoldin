package dev.sldn.anticheat.checks.misc;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.api.Check;
import dev.sldn.anticheat.api.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class AutoClickerCheck extends Check {
    private final Map<UUID, Deque<Long>> clicks = new HashMap<>();

    public AutoClickerCheck(SldnAntiCheat plugin) { super(plugin); }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if (!plugin.getConfig().getBoolean("checks.enabled.AutoClicker", true)) return;
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        clicks.computeIfAbsent(id, k -> new ArrayDeque<>()).addLast(now);

        int winSec = plugin.getConfig().getInt("autoclicker.cpsWindowSeconds",2);
        long cutoff = now - winSec*1000L;
        while (!clicks.get(id).isEmpty() && clicks.get(id).peekFirst() < cutoff) clicks.get(id).removeFirst();

        int cps = clicks.get(id).size()/winSec;
        int maxCPS = plugin.getConfig().getInt("autoclicker.maxCPS",15);
        if (cps > maxCPS) {
            flag(p, CheckType.AUTOCLICKER, "cps="+cps, plugin.getConfig().getInt("autoclicker.vlPerFail",2));
        }

        if (clicks.get(id).size() >= 8) {
            long[] arr = clicks.get(id).stream().mapToLong(l->l).toArray();
            List<Long> diffs = new ArrayList<>();
            for (int i=1;i<arr.length;i++) diffs.add(arr[i]-arr[i-1]);
            double mean = diffs.stream().mapToLong(l->l).average().orElse(0);
            double var = diffs.stream().mapToDouble(d->(d-mean)*(d-mean)).sum()/diffs.size();
            double std = Math.sqrt(var);
            double minStd = plugin.getConfig().getDouble("autoclicker.minStdDev",1.2);
            if (std < minStd) {
                flag(p, CheckType.AUTOCLICKER, "stdDev="+String.format(java.util.Locale.US, "%.2f", std), plugin.getConfig().getInt("autoclicker.vlPerFail",2));
            }
        }
    }
}

package dev.sldn.anticheat.checks.combat;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.api.Check;
import dev.sldn.anticheat.api.CheckType;
import dev.sldn.anticheat.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraCheck extends Check {
    private final Map<UUID, Long> lastSwing = new HashMap<>();
    private final Map<UUID, Integer> targetsThisTick = new HashMap<>();

    public KillAuraCheck(SldnAntiCheat plugin) { super(plugin); }

    @EventHandler
    public void onSwing(PlayerAnimationEvent e){
        lastSwing.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Player p)) return;
        if (!plugin.getConfig().getBoolean("checks.enabled.KillAura", true)) return;
        PlayerData d = plugin.getData(p);
        d.markDamage();

        long worldTick = p.getWorld().getFullTime();
        targetsThisTick.merge(p.getUniqueId(), 1, Integer::sum);
        if (targetsThisTick.get(p.getUniqueId()) > plugin.getConfig().getInt("killaura.maxTargetsPerTick",1)){
            flag(p, CheckType.KILLAURA, "multi-target", plugin.getConfig().getInt("killaura.vlPerFail",2));
        }

        long now = System.currentTimeMillis();
        long last = lastSwing.getOrDefault(p.getUniqueId(), 0L);
        long dtMs = now - last;
        int minTicks = plugin.getConfig().getInt("killaura.minSwingIntervalTicks",2);
        if (dtMs < minTicks*50L){
            flag(p, CheckType.KILLAURA, "swingInterval="+dtMs+"ms", plugin.getConfig().getInt("killaura.vlPerFail",2));
        }
    }

    @Override
    public void tick(Player p) {
        targetsThisTick.remove(p.getUniqueId());
    }
}

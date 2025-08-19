package dev.sldn.anticheat.checks.movement;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.api.Check;
import dev.sldn.anticheat.api.CheckType;
import dev.sldn.anticheat.util.BlockUtil;
import dev.sldn.anticheat.util.EnvUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class SpeedCheck extends Check {
    public SpeedCheck(SldnAntiCheat plugin) { super(plugin); }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if (!plugin.getConfig().getBoolean("checks.enabled.Speed", true)) return;
        if (p.isInsideVehicle() || p.isGliding() || p.isSwimming()) return;

        Vector delta = e.getTo().toVector().subtract(e.getFrom().toVector());
        double hz = Math.hypot(delta.getX(), delta.getZ());
        double bps = hz * 20.0;

        double cap = plugin.getConfig().getDouble("speed.baseMaxBps", 5.8);

        Material below = e.getTo().clone().subtract(0, 1, 0).getBlock().getType();
        if (BlockUtil.isIce(below)) cap *= plugin.getConfig().getDouble("speed.iceMultiplier", 1.35);
        int spd = EnvUtil.speedLevel(p);
        cap += spd * plugin.getConfig().getDouble("speed.speedEffectPerLevel", 0.2);
        int soul = EnvUtil.soulSpeedLevel(p);
        cap += soul * plugin.getConfig().getDouble("speed.soulSpeedBonusPerLevel", 0.5);
        if (EnvUtil.hasDolphinsGrace(p)) cap += plugin.getConfig().getDouble("speed.dolphinsGraceBonus", 1.7);

        if (bps > cap) {
            flag(p, CheckType.SPEED, "bps="+String.format("%.2f/%.2f", bps, cap), plugin.getConfig().getInt("speed.vlPerFail",1));
        }
    }
}

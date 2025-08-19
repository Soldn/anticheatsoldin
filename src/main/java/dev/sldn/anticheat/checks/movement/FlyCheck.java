package dev.sldn.anticheat.checks.movement;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.api.Check;
import dev.sldn.anticheat.api.CheckType;
import dev.sldn.anticheat.data.PlayerData;
import dev.sldn.anticheat.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class FlyCheck extends Check {
    public FlyCheck(SldnAntiCheat plugin) { super(plugin); }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if (!plugin.getConfig().getBoolean("checks.enabled.Fly", true)) return;
        PlayerData d = plugin.getData(p);

        if (p.isFlying() || p.isInsideVehicle() || p.getAllowFlight() || p.getGameMode().name().contains("CREATIVE")) return;
        if (p.isGliding() || d.isGliding() || d.exemptExplosion() || d.exemptElytraWindow()) return;

        double fromY = e.getFrom().getY();
        double toY = e.getTo().getY();
        double dy = toY - fromY;
        if (dy <= 0) return;

        Material below = e.getTo().clone().subtract(0, 1, 0).getBlock().getType();
        if (BlockUtil.isClimbable(below)) return;

        double maxAsc = plugin.getConfig().getDouble("fly.maxAscendPerTick", 0.62);
        if (dy > maxAsc) {
            flag(p, CheckType.FLY, "ascend="+dy, plugin.getConfig().getInt("fly.vlPerFail",1));
        }
    }
}

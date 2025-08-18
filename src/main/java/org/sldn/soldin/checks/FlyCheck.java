package org.sldn.soldin.checks;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyCheck extends Check implements Listener {

    private final Map<UUID, Integer> airTicks = new HashMap<>();

    public FlyCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean hasElytra(Player p) {
        ItemStack chest = p.getInventory().getChestplate();
        return chest != null && chest.getType() == Material.ELYTRA;
    }

    private boolean isLiquid(Block b) {
        Material t = b.getType();
        return t == Material.WATER || t == Material.KELP || t == Material.KELP_PLANT || t == Material.SEAGRASS
                || t == Material.TALL_SEAGRASS || t == Material.LAVA || t == Material.BUBBLE_COLUMN;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getAllowFlight() || p.getGameMode() == GameMode.CREATIVE || p.isInsideVehicle()) return;

        // Ignore when gliding with elytra
        if (p.isGliding() || hasElytra(p)) { airTicks.put(p.getUniqueId(), 0); return; }

        // Ignore when in liquids
        if (isLiquid(e.getTo().getBlock()) || isLiquid(e.getTo().clone().add(0, -1, 0).getBlock())) {
            airTicks.put(p.getUniqueId(), 0); return;
        }

        // If strong upward velocity (knockback/boost), give grace
        Vector v = p.getVelocity();
        if (v.getY() > 0.4) { airTicks.put(p.getUniqueId(), 0); return; }

        boolean nearGround = false;
        for (int y = -1; y >= -2; y--) {
            if (e.getTo().clone().add(0, y, 0).getBlock().getType().isSolid()) { nearGround = true; break; }
        }

        if (!nearGround && p.getVelocity().getY() >= -0.08) {
            int t = airTicks.getOrDefault(p.getUniqueId(), 0) + 1;
            airTicks.put(p.getUniqueId(), t);
            int timeout = plugin.cfg().getInt("anticheat.fly-timeout", 5);
            if (t > timeout * 20) { // seconds to ticks
                plugin.addViolation(p, "Fly", 1);
            }
        } else {
            airTicks.put(p.getUniqueId(), 0);
        }
    }

    @Override
    public int getVL(Player p) { return airTicks.getOrDefault(p.getUniqueId(), 0) / 20; }
}

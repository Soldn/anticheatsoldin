package org.sldn.soldin.checks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.sldn.soldin.SldnSoldin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoSlowCheck extends Check implements Listener {

    private final Map<UUID, Integer> vl = new HashMap<>();
    private final Map<UUID, Boolean> using = new HashMap<>();

    public NoSlowCheck(SldnSoldin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        ItemStack inHand = p.getInventory().getItemInMainHand();
        boolean shouldSlow = inHand != null && (inHand.getType() == Material.SHIELD || inHand.getType().isEdible());
        using.put(p.getUniqueId(), shouldSlow);

        double dx = e.getTo().getX() - e.getFrom().getX();
        double dz = e.getTo().getZ() - e.getFrom().getZ();
        double horizontal = Math.hypot(dx, dz);

        if (shouldSlow && horizontal > 0.36) {
            int v = vl.getOrDefault(p.getUniqueId(), 0) + 1;
            vl.put(p.getUniqueId(), v);
            if (v >= plugin.cfg().getInt("violations.noslow", 6)) {
                plugin.addViolation(p, "NoSlow", 2);
            }
        } else {
            vl.put(p.getUniqueId(), Math.max(0, vl.getOrDefault(p.getUniqueId(), 0) - 1));
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        // while consuming, mark using
        using.put(e.getPlayer().getUniqueId(), true);
    }

    @Override
    public int getVL(Player p) { return vl.getOrDefault(p.getUniqueId(), 0); }
}

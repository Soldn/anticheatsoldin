package org.sldn.soldin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.sldn.soldin.SldnSoldin;

import java.util.*;

public class MiningListener implements Listener {

    private final SldnSoldin plugin;
    private final Map<UUID, Integer> mined = new HashMap<>();
    private final Map<UUID, Integer> rare = new HashMap<>();

    public MiningListener(SldnSoldin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        List<String> tracked = plugin.cfg().getStringList("xray.track-ores");

        mined.put(p.getUniqueId(), mined.getOrDefault(p.getUniqueId(), 0) + 1);
        if (tracked.contains(b.getType().name())) {
            rare.put(p.getUniqueId(), rare.getOrDefault(p.getUniqueId(), 0) + 1);
        }

        int total = mined.getOrDefault(p.getUniqueId(), 1);
        int r = rare.getOrDefault(p.getUniqueId(), 0);
        double rate = (double) r / (double) total;

        if (total >= plugin.cfg().getInt("xray.window-mined-blocks", 200)) {
            if (rate > plugin.cfg().getDouble("xray.max-ore-rate", 0.06)) {
                plugin.addViolation(p, "XRay", 3);
            }
            // reset window
            mined.put(p.getUniqueId(), 0);
            rare.put(p.getUniqueId(), 0);
        }
    }
}

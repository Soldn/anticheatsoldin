package org.sldn.soldin.util;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.boss.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.sldn.soldin.SldnSoldin;

import java.util.*;

public class PunishManager {

    private final SldnSoldin plugin;
    private final Set<UUID> executing = new HashSet<>();

    public PunishManager(SldnSoldin plugin) { this.plugin = plugin; }

    public void startExecution(Player p, boolean testMode) {
        if (executing.contains(p.getUniqueId())) return;
        executing.add(p.getUniqueId());

        int seconds = plugin.cfg().getInt("effects.execution-seconds", 20);
        int levAt = Math.min(seconds, plugin.cfg().getInt("effects.levitation-at", 10));
        float power = (float) plugin.cfg().getDouble("effects.explosion-power", 3.5);
        boolean breakBlocks = plugin.cfg().getBoolean("effects.explosion-damages-blocks", false);

        BossBar bar = Bukkit.createBossBar(SldnSoldin.color(plugin.cfg().getString("messages.start-exec").replace("{seconds}", String.valueOf(seconds))), BarColor.RED, BarStyle.SOLID);
        bar.addPlayer(p);
        bar.setProgress(1.0);

        // Track quit for instant ban
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
                if (e.getPlayer().getUniqueId().equals(p.getUniqueId())) banNow(p, testMode);
            }
        }, plugin);

        new BukkitRunnable() {
            int t = seconds;
            @Override public void run() {
                if (!p.isOnline()) { bar.removeAll(); cancel(); executing.remove(p.getUniqueId()); return; }

                // Bossbar
                bar.setTitle(SldnSoldin.color(plugin.cfg().getString("messages.start-exec").replace("{seconds}", String.valueOf(t))));
                bar.setProgress(Math.max(0.0, (double) t / (double) seconds));

                // Surrounding effects every tick
                p.getWorld().spawnParticle(Particle.SMOKE_LARGE, p.getLocation().add(0, 1, 0), 8, 0.8, 0.6, 0.8, 0.02);
                p.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, p.getLocation().add(0, 0.5, 0), 12, 0.7, 0.4, 0.7, 0.01);
                if (t % 2 == 0) {
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.5f, 0.6f);
                }

                // Levitation + glow + disable damage
                if (t <= levAt) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, false, false, false));
                }

                // At 10 sec remaining: hard launch up
                if (t == 10) {
                    p.setVelocity(new Vector(0, 1.8, 0));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);
                }

                // Final second: explosion and ban
                if (t == 1) {
                    Location loc = p.getLocation();
                    dropInventoryScatter(p, loc);
                    loc.getWorld().createExplosion(loc, power, false, breakBlocks, p);
                    placeGrave(loc);
                    banNow(p, testMode);
                }

                t--;
                if (t <= 0) {
                    bar.removeAll();
                    cancel();
                    executing.remove(p.getUniqueId());
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void banNow(Player p, boolean testMode) {
        if (!p.isOnline()) return;
        if (testMode) {
            // Temp ban for ~1s after kick to emulate flow
            p.kickPlayer("§cТест: бан на 1 секунду");
            Date until = new Date(System.currentTimeMillis() + 1000);
            Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), "Test ban", until, "sldnsoldin");
            Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.getBanList(BanList.Type.NAME).pardon(p.getName()), 40L);
        } else {
            p.kickPlayer("§cЗабанен за читы!");
            Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), plugin.cfg().getString("messages.ban-reason"), null, "sldnsoldin");
        }
    }

    private void dropInventoryScatter(Player p, Location center) {
        World w = center.getWorld();
        // Drop all non-null items from inventory
        for (ItemStack stack : p.getInventory().getContents()) {
            if (stack == null) continue;
            Item item = w.dropItem(center, stack.clone());
            // scatter within ~5 blocks
            double rx = (Math.random() - 0.5) * 5.0;
            double rz = (Math.random() - 0.5) * 5.0;
            item.setVelocity(new Vector(rx, 0.4 + Math.random()*0.4, rz));
        }
        p.getInventory().clear();
    }

    private void placeGrave(Location loc) {
        World w = loc.getWorld();
        int y = w.getHighestBlockYAt(loc);
        Location base = new Location(w, loc.getBlockX(), y, loc.getBlockZ());
        base.getBlock().setType(Material.OAK_SIGN, false);
        if (base.getBlock().getState() instanceof org.bukkit.block.Sign) {
            org.bukkit.block.Sign s = (org.bukkit.block.Sign) base.getBlock().getState();
            s.setLine(0, "§8§m----------------");
            s.setLine(1, "§7Тут был");
            s.setLine(2, "§cпохоронен читер");
            s.setLine(3, "§8§m----------------");
            s.update(true, false);
        }
    }
    }
}

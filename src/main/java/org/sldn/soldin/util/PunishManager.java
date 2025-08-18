package org.sldn.soldin.util;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.boss.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.sldn.soldin.SldnSoldin;

import java.util.*;

public class PunishManager {

    private final SldnSoldin plugin;
    private final Set<UUID> executing = new HashSet<>();

    public PunishManager(SldnSoldin plugin) {
        this.plugin = plugin;
    }

    public void startExecution(Player p, boolean testMode) {
        if (executing.contains(p.getUniqueId())) return;
        executing.add(p.getUniqueId());

        int seconds = testMode ? Math.max(1, plugin.cfg().getInt("thresholds.test-ban-seconds", 1)) : plugin.cfg().getInt("effects.execution-seconds", 20);
        int levAt = Math.min(seconds, plugin.cfg().getInt("effects.levitation-at", 10));
        float power = (float) plugin.cfg().getDouble("effects.explosion-power", 2.0);
        boolean breakBlocks = plugin.cfg().getBoolean("effects.explosion-damages-blocks", false);

        BossBar bar = Bukkit.createBossBar(SldnSoldin.color(plugin.cfg().getString("messages.start-exec").replace("{seconds}", String.valueOf(seconds))), BarColor.RED, BarStyle.SOLID);
        bar.addPlayer(p);
        bar.setProgress(1.0);

        // If player quits -> instant ban
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
                if (e.getPlayer().getUniqueId().equals(p.getUniqueId())) {
                    banNow(p, testMode);
                }
            }
        }, plugin);

        new BukkitRunnable() {
            int t = seconds;

            @Override
            public void run() {
                if (!p.isOnline()) {
                    cancel();
                    bar.removeAll();
                    return;
                }
                bar.setTitle(SldnSoldin.color(plugin.cfg().getString("messages.start-exec").replace("{seconds}", String.valueOf(t))));
                bar.setProgress(Math.max(0.0, (double) t / (double) seconds));

                if (t == seconds) {
                    p.sendMessage(SldnSoldin.color(plugin.cfg().getString("messages.prefix")) + SldnSoldin.color(plugin.cfg().getString("messages.dm-player")));
                }

                if (!testMode) {
                    if (t <= levAt) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1, false, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1, false, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1, false, false, false));
                    }
                }

                if (t == 1) {
                    Location loc = p.getLocation();
                    // Fake explosion visual/sound, optional block damage
                    loc.getWorld().createExplosion(loc, power, false, breakBlocks, p);
                    dropScatter(loc);
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
            p.kickPlayer("§cТест бан");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), "Test ban", new Date(System.currentTimeMillis() + 1000), "sldnsoldin");
                Bukkit.getBanList(BanList.Type.NAME).pardon(p.getName());
            }, 1L);
        } else {
            p.getWorld().spawnParticle(Particle.SMOKE_LARGE, p.getLocation(), 50, 0.6, 0.6, 0.6, 0.02);
            p.kickPlayer("§cЗабанен за читы!");
            Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), plugin.cfg().getString("messages.ban-reason"), null, "sldnsoldin");
        }
    }

    private void dropScatter(Location loc) {
        World w = loc.getWorld();
        // visual scatter: 8 items around
        for (int i = 0; i < 8; i++) {
            ItemStack is = new ItemStack(Material.COBBLESTONE, 1);
            Item item = w.dropItemNaturally(loc, is);
            item.setVelocity(new org.bukkit.util.Vector((Math.random()-0.5)*0.8, 0.4 + Math.random()*0.3, (Math.random()-0.5)*0.8));
        }
    }

    private void placeGrave(Location loc) {
        Location l = loc.clone();
        l.getBlock().setType(Material.OAK_SIGN, false);
        if (l.getBlock().getState() instanceof Sign) {
            Sign s = (Sign) l.getBlock().getState();
            s.setLine(0, "§8§m----------------");
            s.setLine(1, "§7Тут был");
            s.setLine(2, "§cпохоронен читер");
            s.setLine(3, "§8§m----------------");
            s.update(true, false);
        }
    }
}

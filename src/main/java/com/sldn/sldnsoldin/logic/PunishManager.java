package com.sldn.sldnsoldin.logic;

import com.sldn.sldnsoldin.SldnMain;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PunishManager implements Listener {
    private final SldnMain plugin;
    private final Set<UUID> executing = new HashSet<>();

    public PunishManager(SldnMain plugin) { this.plugin = plugin; }

    public boolean isExecuting(UUID id) { return executing.contains(id); }

    public void sendBannedList(CommandSender sender) {
        File f = plugin.getBannedFile();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        java.util.List<String> list = cfg.getStringList("banned");
        if (list.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "Список пуст.");
        } else {
            sender.sendMessage(ChatColor.RED + "Забаненные античитом: " + String.join(", ", list));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (executing.remove(e.getPlayer().getUniqueId())) {
            // авто-бан при выходе
            doBan(e.getPlayer(), true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (isExecuting(p.getUniqueId())) e.setCancelled(true);
        }
    }

    public void startExecution(Player p, int seconds, boolean permaBanAtEnd) {
        if (executing.contains(p.getUniqueId())) return;
        executing.add(p.getUniqueId());

        p.setGlowing(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, seconds * 20, 4, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, seconds * 20, 2, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, seconds * 20, 1, false, false));

        Location startLoc = p.getLocation().clone();
        World w = p.getWorld();

        new BukkitRunnable() {
            int time = seconds;

            @Override
            public void run() {
                if (!p.isOnline()) { cancel(); executing.remove(p.getUniqueId()); return; }

                // actionbar + title
                String title = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("settings.execution-title","&cУбираем читера..."));
                String sub = ChatColor.translateAlternateColorCodes('&',
                        plugin.getConfig().getString("settings.execution-subtitle","&7Осталось %time% сек.").replace("%time%", String.valueOf(time)));
                p.sendTitle(title, sub, 0, 25, 0);
                try { p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                        new net.md_5.bungee.api.chat.TextComponent(ChatColor.RED + "Убираем читера • " + ChatColor.WHITE + time + " сек.")); } catch (Throwable ignored) {}

                // частицы
                w.spawnParticle(Particle.SMOKE_LARGE, p.getLocation(), 10, 0.5, 0.2, 0.5, 0.01);

                if (time == Math.max(1, seconds/2)) {
                    // Подкинуть на середине таймера
                    p.setVelocity(new Vector(0, 1.6, 0));
                }

                if (time <= 0) {
                    // Финал: молния + взрыв + дроп + табличка + бан
                    w.strikeLightningEffect(p.getLocation());
                    w.createExplosion(p.getLocation(), 2.0f, false, false);

                    // выброс инвентаря
                    for (ItemStack it : p.getInventory().getContents()) {
                        if (it == null) continue;
                        Item drop = w.dropItemNaturally(p.getLocation(), it);
                        Vector v = new Vector((Math.random()-0.5) * 0.8, 0.4 + Math.random()*0.6, (Math.random()-0.5) * 0.8);
                        drop.setVelocity(v);
                    }
                    p.getInventory().clear();

                    // табличка на земле
                    Location loc = p.getLocation().clone();
                    Location ground = findGround(loc);
                    if (ground != null) {
                        Block b = ground.getBlock();
                        if (b.getType() == Material.AIR) b.setType(Material.OAK_SIGN);
                        BlockState st = b.getState();
                        if (st instanceof Sign) {
                            Sign sign = (Sign) st;
                            String text = ChatColor.translateAlternateColorCodes('&',
                                    plugin.getConfig().getString("settings.execution-sign", "Тут был похоронен читер %player%")
                                            .replace("%player%", p.getName()));
                            String[] lines = wrap(text, 4);
                            for (int i=0;i<lines.length && i<4;i++) sign.setLine(i, lines[i]);
                            sign.update(true);
                        }
                    }

                    // бан
                    if (permaBanAtEnd) doBan(p, false);

                    // cleanup
                    p.setGlowing(false);
                    executing.remove(p.getUniqueId());
                    cancel();
                    return;
                }
                time--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void doBan(Player p, boolean quit) {
        String reason = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("settings.ban-message","Вы были забанены античитом sldnsoldin!")));
        Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(p.getName(), reason, null, "sldnsoldin");
        p.kickPlayer(reason);

        // Запись в banned.yml
        File f = plugin.getBannedFile();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        java.util.List<String> list = cfg.getStringList("banned");
        if (!list.contains(p.getName())) list.add(p.getName());
        cfg.set("banned", list);
        try { cfg.save(f); } catch (IOException ignored) {}

        // уведомление
        plugin.notifyStaff(ChatColor.RED + "Игрок " + p.getName() + " был казнён античитом" + (quit ? " (вышел во время казни)" : ""));
    }

    private Location findGround(Location start) {
        Location loc = start.clone();
        World w = start.getWorld();
        for (int y = start.getBlockY(); y >= 0; y--) {
            loc.setY(y);
            if (w.getBlockAt(loc).getType().isSolid()) {
                Location above = loc.clone().add(0,1,0);
                return above;
            }
        }
        return start;
    }

    private String[] wrap(String text, int lines) {
        String[] out = new String[lines];
        String[] words = text.split(" ");
        String curr = "";
        int idx = 0;
        for (String w : words) {
            if ((curr + " " + w).length() <= 15) curr = (curr.isEmpty()?w:curr+" "+w);
            else {
                out[idx++] = curr;
                curr = w;
                if (idx >= lines) break;
            }
        }
        if (idx < lines) out[idx] = curr;
        for (int i=0;i<lines;i++) if (out[i]==null) out[i] = "";
        return out;
    }
}

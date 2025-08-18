package org.sldn.soldin;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import org.sldn.soldin.checks.*;
import org.sldn.soldin.listeners.ChatFilterListener;
import org.sldn.soldin.listeners.MiningListener;
import org.sldn.soldin.util.PunishManager;

import java.util.*;

public class SldnSoldin extends JavaPlugin {

    private FileConfiguration cfg;
    private final Map<UUID, Integer> totalVL = new HashMap<>();
    private final Map<UUID, BossBar> activeBars = new HashMap<>();
    private PunishManager punishManager;

    private final List<Check> checks = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cfg = getConfig();
        punishManager = new PunishManager(this);

        // Register checks
        checks.add(new SpeedCheck(this));
        checks.add(new KillAuraCheck(this));
        checks.add(new FlyCheck(this));
        checks.add(new FastBreakCheck(this));

        // Listeners
        getServer().getPluginManager().registerEvents(new ChatFilterListener(this), this);
        getServer().getPluginManager().registerEvents(new MiningListener(this), this);

        // Command
        Objects.requireNonNull(getCommand("sldn")).setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (args.length == 0) {
                    sender.sendMessage("§cUsage: /sldn <test|ban|reload>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    cfg = getConfig();
                    sender.sendMessage(color(cfg.getString("messages.prefix")) + color(cfg.getString("messages.reloaded")));
                    return true;
                }
                if (args[0].equalsIgnoreCase("test")) {
                    if (!(sender instanceof Player)) return true;
                    Player p = (Player) sender;
                    punishManager.startExecution(p, true);
                    return true;
                }
                if (args[0].equalsIgnoreCase("ban")) {
                    if (args.length < 2) {
                        sender.sendMessage("§c/sldn ban <player>");
                        return true;
                    }
                    Player t = Bukkit.getPlayer(args[1]);
                    if (t != null) {
                        t.kickPlayer("§cЗабанен навсегда админом");
                        Bukkit.getBanList(BanList.Type.NAME).addBan(t.getName(), cfg.getString("messages.ban-reason"), null, sender.getName());
                        sender.sendMessage(color(cfg.getString("messages.prefix")) + color(cfg.getString("messages.manual-ban").replace("{player}", t.getName())));
                    } else {
                        sender.sendMessage("§cИгрок не найден.");
                    }
                    return true;
                }
                return true;
            }
        });

        getLogger().info("sldnsoldin enabled");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getLogger().info("sldnsoldin disabled");
    }

    public void addViolation(Player p, String type, int amount) {
        int sum = totalVL.getOrDefault(p.getUniqueId(), 0) + amount;
        totalVL.put(p.getUniqueId(), sum);

        // Notify admins
        if (cfg.getBoolean("chat-filter.notify-admins", true)) {
            String msg = color(cfg.getString("messages.prefix"))
                    + color(cfg.getString("messages.flagged-admin")
                    .replace("{type}", type).replace("{player}", p.getName()).replace("{vl}", String.valueOf(sum)));
            Bukkit.getOnlinePlayers().stream().filter(pl -> pl.hasPermission("sldn.admin")).forEach(adm -> adm.sendMessage(msg));
        }

        int certainty = Math.max(
                Math.max(((SpeedCheck)getCheck(SpeedCheck.class)).getVL(p), ((KillAuraCheck)getCheck(KillAuraCheck.class)).getVL(p)),
                Math.max(((FlyCheck)getCheck(FlyCheck.class)).getVL(p), ((FastBreakCheck)getCheck(FastBreakCheck.class)).getVL(p))
        );

        if (certainty >= cfg.getInt("thresholds.certainty-autokill", 9)) {
            punishManager.startExecution(p, false);
            return;
        }

        if (sum >= cfg.getInt("thresholds.autoban-total", 12)) {
            punishManager.startExecution(p, false);
        }
    }

    public Check getCheck(Class<? extends Check> clazz) {
        for (Check c : checks) {
            if (clazz.isInstance(c)) return c;
        }
        return null;
    }

    public FileConfiguration cfg() { return cfg; }
    public PunishManager punish() { return punishManager; }

    public static String color(String s) {
        return s == null ? "" : s.replace("&", "§");
    }
}

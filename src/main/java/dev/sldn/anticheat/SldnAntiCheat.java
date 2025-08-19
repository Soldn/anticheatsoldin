package dev.sldn.anticheat;

import dev.sldn.anticheat.api.Check;
import dev.sldn.anticheat.api.CheckType;
import dev.sldn.anticheat.checks.combat.KillAuraCheck;
import dev.sldn.anticheat.checks.misc.AutoClickerCheck;
import dev.sldn.anticheat.checks.movement.FlyCheck;
import dev.sldn.anticheat.checks.movement.SpeedCheck;
import dev.sldn.anticheat.data.PlayerData;
import dev.sldn.anticheat.punish.PunishmentManager;
import dev.sldn.anticheat.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class SldnAntiCheat extends JavaPlugin implements Listener {
    private final Map<UUID, PlayerData> data = new HashMap<>();
    private final List<Check> checks = new ArrayList<>();
    private PunishmentManager punish;
    private File messagesFile;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMessages();
        Msg.init(this, messages);
        punish = new PunishmentManager(this);

        registerChecks();
        Bukkit.getPluginManager().registerEvents(this, this);

        // per-tick processing
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                getData(p); // ensure created
                checks.forEach(c -> c.tick(p));
            }
        }, 1L, 1L);

        getLogger().info("SLDN AntiCheat enabled.");
    }

    private void loadMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) saveResource("messages.yml", false);
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void registerChecks() {
        checks.add(new FlyCheck(this));
        checks.add(new SpeedCheck(this));
        checks.add(new KillAuraCheck(this));
        checks.add(new AutoClickerCheck(this));
        checks.forEach(Check::register);
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDN AntiCheat disabled.");
    }

    public PlayerData getData(Player p) {
        return data.computeIfAbsent(p.getUniqueId(), id -> new PlayerData(this, p));
    }

    public Collection<PlayerData> allData() { return data.values(); }

    public PunishmentManager punish() { return punish; }

    public FileConfiguration messages() { return messages; }

    public void reloadAll() {
        reloadConfig();
        loadMessages();
        Msg.init(this, messages);
    }

    // ===== Event bridges for exemptions =====
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            PlayerData d = getData(p);
            d.markDamage();
            DamageCause c = e.getCause();
            if (c == DamageCause.BLOCK_EXPLOSION || c == DamageCause.ENTITY_EXPLOSION) {
                d.markExplosion();
            }
        }
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent e) {
        Player p = (Player) e.getEntity();
        getData(p).markElytraToggle(e.isGliding());
    }

    // ===== Command handling =====
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("sldn")) return false;
        if (!sender.hasPermission("sldn.admin")) {
            sender.sendMessage(Msg.noperms());
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§7/sldn reload §8- перезагрузка");
            sender.sendMessage("§7/sldn vl <игрок> §8- показать VL");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                reloadAll();
                sender.sendMessage(Msg.reloaded());
            }
            case "vl" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cИспользование: /sldn vl <игрок>");
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) {
                    sender.sendMessage("§cИгрок не найден.");
                    return true;
                }
                int vl = getData(target).totalVL();
                sender.sendMessage(Msg.vl(target.getName(), vl));
            }
            default -> sender.sendMessage("§cНеизвестная команда.");
        }
        return true;
    }
}

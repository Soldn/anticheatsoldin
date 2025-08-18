package com.sldn.sldnsoldin;

import com.sldn.sldnsoldin.commands.SldnCommand;
import com.sldn.sldnsoldin.logic.ChatFilterListener;
import com.sldn.sldnsoldin.logic.GuiManager;
import com.sldn.sldnsoldin.logic.PunishManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SldnMain extends JavaPlugin {
    private static SldnMain instance;
    private File logFile;
    private File bannedFile;
    private PunishManager punishManager;
    private GuiManager guiManager;

    public static SldnMain get() { return instance; }
    public File getLogFile() { return logFile; }
    public File getBannedFile() { return bannedFile; }
    public PunishManager punish() { return punishManager; }
    public GuiManager gui() { return guiManager; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // banner
        getLogger().info("[SLDN v6] Античит sldnsoldin успешно загружен!");
        getLogger().info("### разработчик плагина Soldi_n .jar");

        // files
        if (getConfig().getBoolean("logging.enabled", true)) {
            String logPath = getConfig().getString("logging.violations-file", "logs/violations.log");
            logFile = new File(getDataFolder(), logPath);
            if (!logFile.getParentFile().exists()) logFile.getParentFile().mkdirs();
            try { if (!logFile.exists()) logFile.createNewFile(); } catch (IOException ignored) {}
        }
        String bannedPath = getConfig().getString("logging.banned-file", "banned.yml");
        bannedFile = new File(getDataFolder(), bannedPath);
        if (!bannedFile.exists()) saveResource("banned.yml", false);

        // managers
        punishManager = new PunishManager(this);
        guiManager = new GuiManager(this);

        // command
        if (getCommand("sldn") != null)
            getCommand("sldn").setExecutor(new SldnCommand(this));

        // listeners
        Bukkit.getPluginManager().registerEvents(new ChatFilterListener(this), this);
        Bukkit.getPluginManager().registerEvents(guiManager, this);
        Bukkit.getPluginManager().registerEvents(punishManager, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("[SLDN v6] Античит sldnsoldin выгружен.");
    }

    public void logViolation(String player, String check, int vl) {
        if (!getConfig().getBoolean("logging.enabled", true)) return;
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String line = "[" + time + "] Player: " + player + " | Check: " + check + " | VL: " + vl + "\n";
        try (FileWriter fw = new FileWriter(getLogFile(), true)) {
            fw.write(line);
        } catch (IOException ignored) {}
    }

    public void notifyStaff(String message) {
        String pref = org.bukkit.ChatColor.translateAlternateColorCodes('&', getConfig().getString("settings.prefix", "&c[SLDN]&f "));
        String msg = pref + message;
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("sldn.notify"))
                .forEach(p -> p.sendMessage(msg));
        getLogger().info(org.bukkit.ChatColor.stripColor(msg));
    }
}

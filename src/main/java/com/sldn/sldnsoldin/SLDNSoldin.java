package com.sldn.sldnsoldin;

import com.sldn.sldnsoldin.checks.CombatCheck;
import com.sldn.sldnsoldin.checks.FlyCheck;
import com.sldn.sldnsoldin.commands.CommandManager;
import com.sldn.sldnsoldin.utils.LogManager;
import com.sldn.sldnsoldin.utils.PunishManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SLDNSoldin extends JavaPlugin {

    private static SLDNSoldin instance;
    private LogManager logManager;
    private PunishManager punishManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // если появится config.yml
        this.logManager = new LogManager(this);
        this.punishManager = new PunishManager(this);

        getServer().getPluginManager().registerEvents(new FlyCheck(this), this);
        getServer().getPluginManager().registerEvents(new CombatCheck(this), this);

        // Команды
        getCommand("sldn").setExecutor(new CommandManager(this));
        getCommand("ban").setExecutor(new CommandManager(this));
        getCommand("unban").setExecutor(new CommandManager(this));
        getCommand("tempban").setExecutor(new CommandManager(this));

        getLogger().info("SLDNSoldin v1.1.0 enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin v1.1.0 disabled");
    }

    public static SLDNSoldin get() {
        return instance;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }
}
package com.sldn.sldnsoldin;

import com.sldn.sldnsoldin.commands.CommandManager;
import com.sldn.sldnsoldin.checks.CombatCheck;
import com.sldn.sldnsoldin.checks.FlyCheck;
import com.sldn.sldnsoldin.utils.LogManager;
import com.sldn.sldnsoldin.utils.PunishManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SLDNSoldin extends JavaPlugin {

    private static SLDNSoldin instance;
    private LogManager logManager;
    private PunishManager punishManager;

    @Override
    public void onEnable() {
        instance = this;

        // Готовим папку плагина
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Инициализируем менеджеры (их используют Checks и PunishManager)
        this.logManager = new LogManager(this);
        this.punishManager = new PunishManager(this);

        // Регистрируем ивенты античита (если у тебя эти классы есть в проекте)
        getServer().getPluginManager().registerEvents(new FlyCheck(this), this);
        getServer().getPluginManager().registerEvents(new CombatCheck(this), this);

        // Регистрируем команды
        CommandManager cmd = new CommandManager(this);
        getCommand("sldn").setExecutor(cmd);
        getCommand("ban").setExecutor(cmd);
        getCommand("unban").setExecutor(cmd);
        getCommand("tempban").setExecutor(cmd);

        getLogger().info("SLDNSoldin enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin disabled");
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

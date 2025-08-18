package com.sldn.sldnsoldin;

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

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        logManager = new LogManager(this);
        punishManager = new PunishManager(this);

        getLogger().info("SLDNSoldin Anticheat enabled!");
        // TODO: register commands, listeners, checks
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin Anticheat disabled!");
    }

    public static SLDNSoldin getInstance() {
        return instance;
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }
}

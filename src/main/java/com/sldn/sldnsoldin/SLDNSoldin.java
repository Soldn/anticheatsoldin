package com.sldn.sldnsoldin;

import com.sldn.sldnsoldin.utils.LogManager;
import com.sldn.sldnsoldin.utils.PunishManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class SLDNSoldin extends JavaPlugin {
    private LogManager logManager;
    private PunishManager punishManager;

    @Override
    public void onEnable() {
        this.logManager = new LogManager(getDataFolder());
        this.punishManager = new PunishManager(logManager);

        getLogger().info("SLDNSoldin enabled!");
    }

    public LogManager getLogManager() {
        return logManager;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }

    public File getLogFile() {
        return logManager.getLogFile();
    }
}

package com.sldn.sldnsoldin;

import org.bukkit.plugin.java.JavaPlugin;
import com.sldn.sldnsoldin.commands.CommandManager;

public class SLDNSoldin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("sldn").setExecutor(new CommandManager());
        getLogger().info("SLDNSoldin v1.0.0 enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin v1.0.0 disabled!");
    }
}

    private final com.sldn.sldnsoldin.utils.LogManager logManager = new com.sldn.sldnsoldin.utils.LogManager(this);
    private final com.sldn.sldnsoldin.utils.PunishManager punishManager = new com.sldn.sldnsoldin.utils.PunishManager(this);

    public com.sldn.sldnsoldin.utils.LogManager getLogManager() {
        return logManager;
    }

    public com.sldn.sldnsoldin.utils.PunishManager getPunishManager() {
        return punishManager;
    }
    
}
package com.sldn.sldnsoldin;

import org.bukkit.plugin.java.JavaPlugin;
import com.sldn.sldnsoldin.commands.CommandManager;

public class SLDNSoldin extends JavaPlugin {
    private static SLDNSoldin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("SLDNSoldin v2.0 запущен!");
        getCommand("sldn").setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin выключен!");
    }

    public static SLDNSoldin getInstance() {
        return instance;
    }
}

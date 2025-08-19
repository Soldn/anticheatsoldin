package com.sldn.sldnsoldin;

import org.bukkit.plugin.java.JavaPlugin;

public final class SLDNSoldin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SLDNSoldin enabled!");

        saveDefaultConfig();

        getCommand("kickveyp").setExecutor(new KickVeypCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("Команда /kickveyp и античит активированы.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin disabled!");
    }
}

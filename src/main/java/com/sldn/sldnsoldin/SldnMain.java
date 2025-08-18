package com.sldn.sldnsoldin;

import org.bukkit.plugin.java.JavaPlugin;

public class SldnMain extends JavaPlugin {
    private static SldnMain instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getLogger().info("sldnsoldin v3 enabled with warn system and better checks!");
        // Здесь бы подключались слушатели античита и логика казни
    }

    @Override
    public void onDisable() {
        getLogger().info("sldnsoldin v3 disabled!");
    }

    public static SldnMain getInstance() {
        return instance;
    }
}

package com.sldn.sldnsoldin;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("sldnsoldin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("sldnsoldin disabled!");
    }
}

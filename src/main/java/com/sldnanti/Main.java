
/*
 * Плагин Sldn разработан Soldi_n .jar
 */
package com.sldnanti;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("SldnantiSoldin включён!");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("SldnantiSoldin выключен!");
    }
}

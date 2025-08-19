package com.sldn.sldnsoldin;

import org.bukkit.plugin.java.JavaPlugin;
import com.sldn.sldnsoldin.checks.MovementCheck;

public class SLDNSoldin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("SLDN AntiCheat enabled!");
        getServer().getPluginManager().registerEvents(new MovementCheck(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDN AntiCheat disabled!");
    }
}

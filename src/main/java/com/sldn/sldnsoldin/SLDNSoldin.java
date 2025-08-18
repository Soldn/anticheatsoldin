
package com.sldn.sldnsoldin;

import com.sldn.sldnsoldin.checks.CombatCheck;
import com.sldn.sldnsoldin.checks.MovementCheck;
import com.sldn.sldnsoldin.util.Logs;
import com.sldn.sldnsoldin.util.StatusTracker;
import com.sldn.sldnsoldin.util.ViolationManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SLDNSoldin extends JavaPlugin {

    private static SLDNSoldin instance;
    private Logs logs;
    private ViolationManager vio;
    private StatusTracker tracker;

    public static SLDNSoldin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        logs = new Logs(this);
        vio = new ViolationManager();
        tracker = new StatusTracker();

        // Регистрация слушателей
        Bukkit.getPluginManager().registerEvents(new MovementCheck(this, logs, vio, tracker), this);
        Bukkit.getPluginManager().registerEvents(new CombatCheck(this, logs, vio, tracker), this);

        getLogger().info("SLDNSoldin AntiCheat v1.1 включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin AntiCheat v1.1 выключен!");
    }
}

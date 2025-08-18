package sldn.sldnsoldin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SldnSoldinPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("[SLDN v6] Античит sldnsoldin успешно загружен!");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("[SLDN v6] Античит sldnsoldin выгружен.");
    }
}

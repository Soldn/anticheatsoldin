package com.sldn.sldnsoldin;

import com.sldn.sldnsoldin.commands.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;

public class SLDNSoldin extends JavaPlugin {
    private File logFile;

    @Override
    public void onEnable() {
        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        logFile = new File(pluginFolder, "logs.txt");
        if (!logFile.exists()) {
            try { logFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }

        CommandManager cmdManager = new CommandManager(this);
        getCommand("sldn").setExecutor(cmdManager);
        getCommand("ban").setExecutor(cmdManager);
        getCommand("unban").setExecutor(cmdManager);
        getCommand("tempban").setExecutor(cmdManager);

        getLogger().info("SLDNSoldin включен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin выключен!");
    }

    public File getLogFile() { return logFile; }
}

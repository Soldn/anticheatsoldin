package com.sldn.sldnsoldin.utils;

import com.sldn.sldnsoldin.SLDNSoldin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogManager {

    private final SLDNSoldin plugin;
    private final File logFile;

    public LogManager(SLDNSoldin plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), "logs.txt");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
    }

    public void log(String message) {
        String timestamp = "[" + LocalDateTime.now() + "] " + message;
        plugin.getLogger().info(timestamp);

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(timestamp + "\n");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write to log file: " + e.getMessage());
        }
    }
}

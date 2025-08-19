package com.sldn.sldnsoldin.utils;

import com.sldn.sldnsoldin.SLDNSoldin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

    private final SLDNSoldin plugin;
    private final File file;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LogManager(SLDNSoldin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "logs.txt");
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
    }

    public void log(String player, String cat, String message) {
        String line = "[" + fmt.format(new Date()) + "][" + cat + "][" + player + "] " + message;
        Bukkit.getConsoleSender().sendMessage(line);
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(line + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
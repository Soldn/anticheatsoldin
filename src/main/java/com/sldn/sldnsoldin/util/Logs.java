
package com.sldn.sldnsoldin.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs {
    private final JavaPlugin plugin;
    private final boolean toConsole;
    private final File logFile;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Logs(JavaPlugin plugin) {
        this.plugin = plugin;
        this.toConsole = plugin.getConfig().getBoolean("logging.alsoConsole", true);
        String fileName = plugin.getConfig().getString("logging.fileName", "logs.txt");
        File data = plugin.getDataFolder();
        if (!data.exists()) data.mkdirs();
        this.logFile = new File(data, fileName);
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Не удалось создать лог-файл: " + e.getMessage());
        }
    }

    public synchronized void detection(String player, String type, String details) {
        if (!plugin.getConfig().getBoolean("logging.logDetections", true)) return;
        write("DETECT", player, type, details);
    }

    public synchronized void ban(String player, String type, String details) {
        if (!plugin.getConfig().getBoolean("logging.logBans", true)) return;
        write("BAN", player, type, details);
    }

    private void write(String level, String player, String type, String details) {
        String line = String.format("[%s] [%s] Игрок %s — Нарушение: %s — %s",
                fmt.format(new Date()), level, player, type, details);
        if (toConsole) {
            Bukkit.getLogger().info(line);
        }
        try (Writer w = new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8)) {
            w.write(line + System.lineSeparator());
        } catch (IOException e) {
            plugin.getLogger().warning("Ошибка записи в лог-файл: " + e.getMessage());
        }
    }
}

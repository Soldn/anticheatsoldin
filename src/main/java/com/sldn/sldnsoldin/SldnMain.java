package com.sldn.sldnsoldin;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.entity.Player;

public class SldnMain extends JavaPlugin {
    private static SldnMain instance;
    private File logFile;
    private File bannedFile;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Лог-файл
        logFile = new File(getDataFolder(), getConfig().getString("logging.violations-file"));
        if (!logFile.getParentFile().exists()) logFile.getParentFile().mkdirs();
        try {
            if (!logFile.exists()) logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Файл банов
        bannedFile = new File(getDataFolder(), getConfig().getString("logging.banned-file"));
        if (!bannedFile.exists()) {
            saveResource("banned.yml", false);
        }

        getLogger().info("sldnsoldin v5 enabled with logging and banned list!");
    }

    @Override
    public void onDisable() {
        getLogger().info("sldnsoldin v5 disabled!");
    }

    public static SldnMain getInstance() {
        return instance;
    }

    public void logViolation(Player p, String check, int vl) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String line = "[" + time + "] Player: " + p.getName() + " | Check: " + check + " | VL: " + vl + "\n";
        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

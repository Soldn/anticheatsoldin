package com.sldn.sldnsoldin.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {
    private final File logFile;

    public LogManager(File dataFolder) {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.logFile = new File(dataFolder, "logs.txt");
    }

    // старый вариант
    public void log(String message) {
        write(message);
    }

    // новый вариант (с доп. параметрами)
    public void log(String player, String check, String reason) {
        String formatted = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] "
                + player + " | " + check + " | " + reason;
        write(formatted);
    }

    private void write(String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getLogFile() {
        return logFile;
    }
}

package com.sldn.sldnsoldin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class LogManager {
    private final File logFile;

    public LogManager(File dataFolder) {
        this.logFile = new File(dataFolder, "logs.txt");

        try {
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getLogFile() {
        return logFile;
    }

    public void log(String message) {
        try {
            Files.write(
                logFile.toPath(),
                (message + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

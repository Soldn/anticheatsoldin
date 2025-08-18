package com.sldn.sldnsoldin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class SLDNSoldin extends JavaPlugin implements Listener {

    private File logFile;

    @Override
    public void onEnable() {
        // Регистрируем слушатели
        Bukkit.getPluginManager().registerEvents(this, this);

        // Создаём файл логов
        logFile = new File(getDataFolder(), "logs.txt");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getLogger().info("SLDNSoldin AntiCheat запущен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SLDNSoldin AntiCheat выключен!");
    }

    /**
     * Логирование в файл и консоль
     */
    private void logSuspicious(Player player, String reason) {
        String message = "[" + LocalDateTime.now() + "] " + player.getName() + ": " + reason;
        getLogger().warning(message);
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверка на Fly (не баним, только логируем)
     */
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Игнорируем если игрок в креативе или на элитрах
        if (player.isFlying() || player.isGliding()) {
            return;
        }

        double yDiff = event.getTo().getY() - event.getFrom().getY();
        if (yDiff > 0.5) { // подозрительный резкий взлёт
            logSuspicious(player, "Возможный Fly (yDiff=" + yDiff + ")");
            player.sendMessage(ChatColor.YELLOW + "[AntiCheat] Подозрительное движение зафиксировано.");
        }
    }

    /**
     * Проверка на KillAura / слишком быстрое кликанье
     */
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        long now = System.currentTimeMillis();
        long last = lastHitTime.getOrDefault(player.getName(), 0L);
        long diff = now - last;

        if (diff < 50) { // меньше 50 мс между ударами (нереально)
            logSuspicious(player, "Возможная KillAura (интервал=" + diff + "мс)");
            player.sendMessage(ChatColor.YELLOW + "[AntiCheat] Подозрительные удары зафиксированы.");
        }

        lastHitTime.put(player.getName(), now);
    }

    // Храним время последнего удара игроков
    private final java.util.Map<String, Long> lastHitTime = new java.util.HashMap<>();
}

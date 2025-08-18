package com.sldn.sldnsoldin.logic;

import com.sldn.sldnsoldin.SldnMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatFilterListener implements Listener {
    private final SldnMain plugin;
    public ChatFilterListener(SldnMain plugin) { this.plugin = plugin; }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!plugin.getConfig().getBoolean("chat-filter.enabled", true)) return;
        List<String> bad = plugin.getConfig().getStringList("chat-filter.blocked-words");
        String msg = e.getMessage();
        String clean = msg;
        for (String w : bad) {
            if (w == null || w.isEmpty()) continue;
            clean = clean.replaceAll("(?i)"+java.util.regex.Pattern.quote(w), "***");
        }
        if (!clean.equals(msg)) {
            plugin.logViolation(e.getPlayer().getName(), "ChatFilter", 1);
        }
        e.setMessage(clean);
    }
}

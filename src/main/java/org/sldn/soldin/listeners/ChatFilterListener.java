package org.sldn.soldin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.sldn.soldin.SldnSoldin;

import java.util.List;

public class ChatFilterListener implements Listener {

    private final SldnSoldin plugin;

    public ChatFilterListener(SldnSoldin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!plugin.cfg().getBoolean("chat-filter.enable", true)) return;
        String msg = e.getMessage();
        List<String> bad = plugin.cfg().getStringList("chat-filter.blocked-words");
        boolean cancel = false;
        for (String b : bad) {
            if (b == null || b.isEmpty()) continue;
            if (msg.toLowerCase().contains(b.toLowerCase())) {
                cancel = true; break;
            }
        }
        if (!cancel && plugin.cfg().getBoolean("chat-filter.block-caps", true) && msg.length() >= plugin.cfg().getInt("chat-filter.caps-min-length", 6)) {
            int upp = 0;
            for (char c : msg.toCharArray()) if (Character.isLetter(c) && Character.isUpperCase(c)) upp++;
            if (upp > msg.length() * 0.7) cancel = true;
        }
        if (cancel) {
            e.setCancelled(true);
            if (plugin.cfg().getBoolean("chat-filter.notify-admins", true)) {
                String out = SldnSoldin.color(plugin.cfg().getString("messages.prefix")) + "§eФильтр чата: §c" + e.getPlayer().getName() + "§7: §f" + msg;
                Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission("sldn.admin")).forEach(p -> p.sendMessage(out));
            }
            e.getPlayer().sendMessage(SldnSoldin.color(plugin.cfg().getString("messages.prefix")) + "§cСообщение отклонено фильтром.");
        }
    }
}

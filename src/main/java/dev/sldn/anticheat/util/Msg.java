package dev.sldn.anticheat.util;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.api.CheckType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Msg {
    private static SldnAntiCheat plugin; private static FileConfiguration m;

    public static void init(SldnAntiCheat pl, FileConfiguration messages) { plugin = pl; m = messages; }

    private static String cc(String s){ return ChatColor.translateAlternateColorCodes('&', s).replace("§", "§"); }

    private static String prefix() { return m.getString("prefix", "[SLDN]"); }

    public static void flag(Player p, CheckType t, int vl){
        String key = "alerts."+t.name();
        String msg = m.getString(key, prefix()+" "+p.getName()+" flagged "+t.name()+" VL="+vl);
        msg = msg.replace("%prefix%", prefix())
                 .replace("%player%", p.getName())
                 .replace("%reason%", t.name())
                 .replace("%vl%", String.valueOf(vl));
        String out = msg;
        plugin.getServer().getOnlinePlayers().stream().filter(pl -> pl.hasPermission("sldn.admin")).forEach(adm -> adm.sendMessage(out));
        plugin.getLogger().info(ChatColor.stripColor(out));
    }

    public static void punish(Player p, String modeKey, CheckType t, int vl){
        String key = "punishments."+modeKey;
        String msg = m.getString(key, prefix()+" punished %player% for %reason% (VL=%vl%)");
        msg = msg.replace("%prefix%", prefix())
                 .replace("%player%", p.getName())
                 .replace("%reason%", t.name())
                 .replace("%vl%", String.valueOf(vl));
        plugin.getServer().broadcastMessage(msg);
    }

    public static String reloaded() { return m.getString("generic.reloaded", prefix()+" reloaded."); }
    public static String noperms() { return m.getString("generic.noperms", prefix()+" no permissions."); }
    public static String vl(String player, int vl) {
        String s = m.getString("generic.vl", prefix()+" VL %player%: %vl%");
        return s.replace("%player%", player).replace("%vl%", String.valueOf(vl)).replace("%prefix%", prefix());
    }
}

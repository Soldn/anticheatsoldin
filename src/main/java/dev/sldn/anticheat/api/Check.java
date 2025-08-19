package dev.sldn.anticheat.api;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.data.PlayerData;
import dev.sldn.anticheat.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Check implements Listener {
    protected final SldnAntiCheat plugin;

    protected Check(SldnAntiCheat plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    protected void flag(Player p, CheckType type, String reason, int vlDelta) {
        PlayerData d = plugin.getData(p);
        int vl = d.addVL(type, vlDelta);
        Msg.flag(p, type, vl);
        plugin.punish().consider(p, type, reason, vl);
        plugin.punish().log(p, type, reason, vl);
    }

    public void tick(Player p) { /* optional per-tick */ }
}

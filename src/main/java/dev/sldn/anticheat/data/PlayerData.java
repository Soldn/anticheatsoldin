package dev.sldn.anticheat.data;

import dev.sldn.anticheat.SldnAntiCheat;
import dev.sldn.anticheat.api.CheckType;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final SldnAntiCheat plugin;
    private final UUID id;
    private long lastDamageMs = 0L;
    private long lastExplosionMs = 0L;
    private long lastElytraToggleMs = 0L;
    private boolean gliding = false;
    private final Map<CheckType, Integer> vl = new EnumMap<>(CheckType.class);

    public PlayerData(SldnAntiCheat plugin, Player p) {
        this.plugin = plugin; this.id = p.getUniqueId();
        for (CheckType t : CheckType.values()) vl.put(t, 0);
    }

    public UUID id() { return id; }

    public void markDamage() { lastDamageMs = System.currentTimeMillis(); }
    public void markExplosion() { lastExplosionMs = System.currentTimeMillis(); }
    public void markElytraToggle(boolean gliding) {
        this.gliding = gliding; this.lastElytraToggleMs = System.currentTimeMillis();
    }
    public boolean isGliding() { return gliding; }

    public boolean exemptCombat() {
        long sec = plugin.getConfig().getInt("exempt.combatSecondsAfterDamage",3);
        return System.currentTimeMillis() - lastDamageMs < sec*1000L;
    }

    public boolean exemptExplosion() {
        long sec = plugin.getConfig().getInt("exempt.movementSecondsAfterExplosion",3);
        return System.currentTimeMillis() - lastExplosionMs < sec*1000L;
    }

    public boolean exemptElytraWindow() {
        long sec = plugin.getConfig().getInt("exempt.elytraMaxGlideSecondsAfterToggle",2);
        return System.currentTimeMillis() - lastElytraToggleMs < sec*1000L;
    }

    public int addVL(CheckType type, int delta) {
        int now = vl.getOrDefault(type, 0) + delta;
        vl.put(type, now);
        return totalVL();
    }

    public int totalVL() { return vl.values().stream().mapToInt(i->i).sum(); }
}

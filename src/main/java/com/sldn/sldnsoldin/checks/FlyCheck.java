package com.sldn.sldnsoldin.checks;

import com.sldn.sldnsoldin.SLDNSoldin;
import com.sldn.sldnsoldin.utils.LogManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyCheck implements Listener {

    private final SLDNSoldin plugin;
    private final LogManager logs;
    // метка "иммунитета" после TNT/взрыва/толчка
    private final Map<UUID, Long> knockImmunity = new HashMap<>();

    public FlyCheck(SLDNSoldin plugin) {
        this.plugin = plugin;
        this.logs = plugin.getLogManager();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            knockImmunity.put(e.getEntity().getUniqueId(), System.currentTimeMillis() + 4000L); // 4 сек иммунитет
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        // Легальные исключения
        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) return;
        if (p.isFlying()) return; // у креатива
        if (p.isGliding() && hasElytra(p)) return; // легальный полет на элитрах
        if (System.currentTimeMillis() < knockImmunity.getOrDefault(p.getUniqueId(), 0L)) return; // после взрыва

        // Простая эвристика: если игрок в воздухе слишком долго без падения
        Vector vel = p.getVelocity();
        boolean onGround = p.isOnGround();
        double dy = vel.getY();
        double distanceY = Math.abs(e.getTo().getY() - e.getFrom().getY());

        if (!onGround && dy == 0.0 && distanceY == 0.0) {
            // висит в воздухе неподвижно > N тиков
            int count = SuspicionCounter.inc(p.getUniqueId(), "fly_hover");
            if (count % 10 == 0) {
                logs.log(p.getName(), "F", "Подозрительное зависание в воздухе (" + count + " тиков)");
            }
            if (count > 40) { // ~2 сек
                plugin.getPunishManager().flag(p, "Fly", "Зависание без причин", false);
            }
        } else {
            SuspicionCounter.reset(p.getUniqueId(), "fly_hover");
        }
    }

    private boolean hasElytra(Player p) {
        ItemStack chest = p.getInventory().getChestplate();
        return chest != null && chest.getType() == Material.ELYTRA;
    }
}
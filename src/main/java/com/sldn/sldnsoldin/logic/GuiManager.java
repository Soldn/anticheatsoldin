package com.sldn.sldnsoldin.logic;

import com.sldn.sldnsoldin.SldnMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GuiManager implements Listener {
    private final SldnMain plugin;
    private final String TITLE = ChatColor.DARK_RED + "SLDN AntiCheat";

    public GuiManager(SldnMain plugin) { this.plugin = plugin; }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        inv.setItem(10, toggleItem("anticheat.fly-check", ChatColor.AQUA + "Анти-Флай", Material.FEATHER));
        inv.setItem(11, toggleItem("anticheat.kill-aura-check", ChatColor.RED + "Анти-Киллаура", Material.DIAMOND_SWORD));
        inv.setItem(12, toggleItem("anticheat.auto-clicker-check", ChatColor.GREEN + "Анти-Кликер", Material.STONE_BUTTON));
        inv.setItem(13, toggleItem("chat-filter.enabled", ChatColor.YELLOW + "Чат-фильтр", Material.PAPER));
        inv.setItem(14, toggleItem("anticheat.speed-check", ChatColor.BLUE + "Анти-Спид", Material.SUGAR));
        p.openInventory(inv);
    }

    private ItemStack toggleItem(String path, String name, Material mat) {
        boolean on = plugin.getConfig().getBoolean(path, true);
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(name + ChatColor.GRAY + " [" + (on ? ChatColor.GREEN + "ВКЛ" : ChatColor.RED + "ВЫКЛ") + ChatColor.GRAY + "]");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "ЛКМ — переключить", ChatColor.DARK_GRAY + path));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        it.setItemMeta(meta);
        return it;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(TITLE)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta.getLore() == null || meta.getLore().isEmpty()) return;
        String path = ChatColor.stripColor(meta.getLore().get(meta.getLore().size()-1)).trim();
        boolean newVal = !plugin.getConfig().getBoolean(path, true);
        plugin.getConfig().set(path, newVal);
        plugin.saveConfig();
        open((Player) e.getWhoClicked());
    }
}

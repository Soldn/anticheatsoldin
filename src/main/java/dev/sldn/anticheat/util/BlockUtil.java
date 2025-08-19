package dev.sldn.anticheat.util;

import org.bukkit.Material;

public class BlockUtil {
    public static boolean isClimbable(Material m){
        return m == Material.LADDER || m == Material.VINE || m.name().contains("SCAFFOLDING");
    }
    public static boolean isIce(Material m){ return m.name().contains("ICE"); }
    public static boolean isSoul(Material m){ return m.name().contains("SOUL"); }
}

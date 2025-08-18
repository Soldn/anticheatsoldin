package org.sldn.soldin.checks;

import org.bukkit.entity.Player;
import org.sldn.soldin.SldnSoldin;

public abstract class Check {
    protected final SldnSoldin plugin;
    public Check(SldnSoldin plugin){ this.plugin = plugin; }
    public abstract int getVL(Player p);
}

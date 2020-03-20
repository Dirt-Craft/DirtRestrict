package net.dirtcraft.dirtrestrict;

import net.dirtcraft.dirtrestrict.Command.BannedItemCommand;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import org.bukkit.plugin.java.JavaPlugin;

public final class DirtRestrict extends JavaPlugin {
    private static DirtRestrict INSTANCE;
    private RestrictionList restrictions;

    @Override
    public void onEnable() {
        restrictions = new RestrictionList(this);
        this.getCommand("bannedItems").setExecutor(new BannedItemCommand());
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public RestrictionList getRestrictions(){
        return restrictions;
    }

    public static DirtRestrict getInstance(){
        return INSTANCE;
    }
}

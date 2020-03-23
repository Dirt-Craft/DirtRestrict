package net.dirtcraft.dirtrestrict;

import net.dirtcraft.dirtrestrict.Command.BannedItemCommand;
import net.dirtcraft.dirtrestrict.Command.DirtRestrictCommand;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.Handlers.SoundHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class DirtRestrict extends JavaPlugin {
    private static DirtRestrict INSTANCE;
    private RestrictionList restrictions;
    private SoundHandler soundHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;
        restrictions = new RestrictionList(this);
        soundHandler = new SoundHandler();
        this.getCommand("bannedItems").setExecutor(new BannedItemCommand());
        this.getCommand("dirtrestrict").setExecutor(new DirtRestrictCommand());

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

    private void registerHandler(Listener listener){
        Bukkit.getPluginManager().registerEvents(listener,this);
    }

    public SoundHandler getSoundHandler() {
        return soundHandler;
    }
}

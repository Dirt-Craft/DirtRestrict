package net.dirtcraft.dirtrestrict;

import net.dirtcraft.dirtrestrict.Command.BannedItemCommand;
import net.dirtcraft.dirtrestrict.Command.DirtRestrictCommand;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import net.dirtcraft.dirtrestrict.Handlers.Restrictions.*;
import net.dirtcraft.dirtrestrict.Handlers.SoundHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class DirtRestrict extends JavaPlugin {
    private List<RestrictionHandler> handlers;
    private PluginManager manager;
    private static DirtRestrict INSTANCE;
    private RestrictionList restrictions;
    private SoundHandler soundHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;
        restrictions = new RestrictionList(this);
        soundHandler = new SoundHandler();
        manager = Bukkit.getPluginManager();
        handlers = Arrays.asList(
                new BlockBreakHandler(),
                new BlockPlaceHandler(),
                new BrewingHandler(),
                new CraftingHandler(),
                new CreativeHandler(),
                new DropListener(),
                new OwnershipHandler(),
                new PickupListener(),
                new SmeltingHandler(),
                new UsageHandler()
        );
        handlers.forEach(h->manager.registerEvents(h, this));
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

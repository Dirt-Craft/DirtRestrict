package net.dirtcraft.dirtrestrict;

import net.dirtcraft.dirtrestrict.Command.BannedItemCommand;
import net.dirtcraft.dirtrestrict.Command.Editor.EditorBase;
import net.dirtcraft.dirtrestrict.Command.Editor.EditorTabCompleter;
import net.dirtcraft.dirtrestrict.Configuration.AdminPreferences;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import net.dirtcraft.dirtrestrict.Handlers.Restrictions.*;
import net.dirtcraft.dirtrestrict.Handlers.SoundHandler;
import net.dirtcraft.dirtrestrict.Utility.RecipeHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class DirtRestrict extends JavaPlugin {
    private PluginManager manager;
    private static DirtRestrict INSTANCE;
    private RestrictionList restrictions;
    private AdminPreferences preferences;
    private SoundHandler soundHandler;
    private RecipeHelper recipeHelper;

    {
        INSTANCE = this;
        restrictions = new RestrictionList(this);
        preferences = new AdminPreferences(this);
        soundHandler = new SoundHandler();
        recipeHelper = new RecipeHelper();
    }

    @Override
    public void onEnable() {
        manager = Bukkit.getPluginManager();
        List<Listener> handlers = Arrays.asList(
                new BlockBreakHandler(),
                new BlockPlaceHandler(),
                new BrewingHandler(),
                new CraftingHandler(),
                new CreativeHandler(),
                new DropListener(),
                new OwnershipHandler(),
                new PickupListener(),
                new SmeltingHandler(),
                new UsageHandler(),
                preferences
        );
        handlers.forEach(h->manager.registerEvents(h, this));
        restrictions.getRestrictions()
                .keySet()
                .stream()
                .filter(restrictions::isCraftDisabled)
                .peek(recipeHelper::removeCraftingRecipe)
                .forEach(recipeHelper::removeSmeltingRecipe);
        this.getCommand(BannedItemCommand.ALIAS).setExecutor(new BannedItemCommand());
        this.getCommand(EditorBase.ALIAS).setExecutor(new EditorBase());
        this.getCommand(EditorBase.ALIAS).setTabCompleter(new EditorTabCompleter());
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

    public SoundHandler getSoundHandler() {
        return soundHandler;
    }

    public AdminPreferences getPreferences() {
        return preferences;
    }

    public RecipeHelper getRecipeHelper() {
        return recipeHelper;
    }
}

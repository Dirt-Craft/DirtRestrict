package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class RestrictionList {
    public static final String separator = ":";
    private HoconConfigurationLoader loader;
    private ConfigurationNode node;
    private Map<String, Restriction> restrictions;
    private final DirtRestrict plugin;

    public RestrictionList (DirtRestrict plugin){
        this.plugin = plugin;
        final File loc = new File(plugin.getDataFolder(), "Restrictions.hocon");
        plugin.getDataFolder().mkdirs();
        loader = HoconConfigurationLoader.builder()
                .setFile(loc)
                .build();
        try{
            node = loader.load(loader.getDefaultOptions().setShouldCopyDefaults(true));
            //noinspection UnstableApiUsage
            restrictions = node.getValue(new TypeToken<Map<String, Restriction>>(){}, new HashMap<>());
            loader.save(node);
        } catch (IOException | ObjectMappingException e){
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void save(){
        try {
            //noinspection UnstableApiUsage
            node.setValue(new TypeToken<Map<String, Restriction>>(){}, restrictions);
            loader.save(node);
        } catch (IOException | ObjectMappingException e){
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void reload(){
        try{
        node = loader.load(loader.getDefaultOptions().setShouldCopyDefaults(true));
            //noinspection UnstableApiUsage
            restrictions = node.getValue(new TypeToken<Map<String, Restriction>>(){}, new HashMap<>());
        } catch (IOException | ObjectMappingException e){
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<Restriction> getRestriction(ItemStack is){
        return Optional.ofNullable(restrictions.getOrDefault(getEntry(is), null));
    }

    public void addBan(ItemStack item){
        Restriction restriction = new Restriction(item, "", RestrictionType.values());
        restrictions.put(restriction.getIndex(), restriction);
    }

    public boolean updateBanType(ItemStack itemStack, RestrictionType type){
        Restriction restriction = restrictions.get(getEntry(itemStack));
        if (restriction == null) return false;
        restriction.toggleRestrictions(type);
        save();
        return true;
    }

    public boolean updateBanReason(ItemStack itemStack, String reason){
        Restriction restriction = restrictions.get(getEntry(itemStack));
        if (restriction == null) return false;
        restriction.setReason(reason);
        save();
        return true;
    }

    public void revokeBan(ItemStack itemStack, String reason){
        restrictions.remove(getEntry(itemStack));
        save();
    }

    private String getEntry(ItemStack is){
        return is.getType().name() + ":" + is.getDurability();
    }
}

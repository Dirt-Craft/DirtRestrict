package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionType;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.ItemKeySerializer;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public class RestrictionList {
    private final DirtRestrict plugin;
    private final AtomicBoolean isDirty = new AtomicBoolean();
    private final AtomicBoolean saving = new AtomicBoolean();
    private final HoconConfigurationLoader loader;
    private ConfigurationNode node;
    private Map<ItemKey, Restriction> restrictions;

    public RestrictionList (DirtRestrict plugin){
        this.plugin = plugin;
        final File loc = new File(plugin.getDataFolder(), "Restrictions.hocon");
        plugin.getDataFolder().mkdirs();
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemKey.class), new ItemKeySerializer());
        loader = HoconConfigurationLoader.builder()
                .setFile(loc)
                .build();
        try{
            node = loader.load(loader.getDefaultOptions().setShouldCopyDefaults(true));
            restrictions = node.getValue(new TypeToken<Map<ItemKey, Restriction>>(){}, new HashMap<>());
            loader.save(node);
        } catch (IOException | ObjectMappingException e){
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void save(){
        isDirty.set(true);
        if (saving.getAndSet(true)) return;
        CompletableFuture.supplyAsync(()->{
            while (true) {
                try {
                    node.setValue(new TypeToken<Map<ItemKey, Restriction>>() {}, restrictions);
                    loader.save(node);
                    isDirty.set(false);
                    Thread.sleep(5000);
                    if (!isDirty.get()){
                        saving.set(false);
                        return true;
                    }
                } catch (IOException | ObjectMappingException | InterruptedException e) {
                    plugin.getLogger().log(Level.SEVERE, e.getMessage());
                    e.printStackTrace();
                    saving.set(false);
                    return false;
                }
            }
        });
    }

    /*
    public void reload(){
        try{
            node = loader.load(loader.getDefaultOptions().setShouldCopyDefaults(true));
            //noinspection UnstableApiUsage
            restrictions = node.getValue(new TypeToken<Map<String, Restriction>>(){}, new HashMap<>());
        } catch (IOException | ObjectMappingException e) {
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }
     */

    public void addBan(ItemKey item){
        restrictions.put(item, new Restriction());
    }

    public boolean updateBanType(ItemKey item, RestrictionType type){
        Restriction restriction = restrictions.get(item);
        if (restriction == null) return false;
        restriction.toggleRestrictions(type);
        save();
        return true;
    }

    public boolean updateBanReason(ItemKey item, String reason){
        Restriction restriction = restrictions.get(item);
        if (restriction == null) return false;
        restriction.setReason(reason);
        save();
        return true;
    }

    public void revokeBan(ItemKey item){
        restrictions.remove(item);
        save();
    }

    public Optional<Restriction> getRestriction(ItemKey item){
        return Optional.ofNullable(restrictions.getOrDefault(item, null));
    }
}

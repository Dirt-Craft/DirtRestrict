package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.EnumSetSerializer;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.HashSetSerializer;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.ItemKeySerializer;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.UUIDSerializer;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@SuppressWarnings({"UnstableApiUsage"})
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
        final TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers();
        serializers.registerType(new TypeToken<EnumSet<RestrictionTypes>>(){}, new EnumSetSerializer<>());
        serializers.registerType(new TypeToken<HashSet<?>>(){}, new HashSetSerializer());
        serializers.registerType(new TypeToken<ItemKey>(){}, new ItemKeySerializer());
        serializers.registerType(new TypeToken<UUID>(){}, new UUIDSerializer());
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
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, e.getMessage());
                    e.printStackTrace();
                    saving.set(false);
                    return false;
                }
            }
        });
    }

    public boolean addBan(ItemKey item){
        if (restrictions.containsKey(item)) return false;
        restrictions.put(item, new Restriction());
        save();
        return true;
    }

    public Optional<Boolean> toggleBanType(ItemKey item, RestrictionTypes type){
        Restriction restriction = restrictions.get(item);
        if (restriction == null) return Optional.empty();
        final boolean val = restriction.toggleRestrictions(type);
        save();
        return Optional.of(val);
    }

    public boolean updateBanReason(ItemKey item, String reason){
        Restriction restriction = restrictions.get(item);
        if (restriction == null) return false;
        restriction.setReason(reason);
        save();
        return true;
    }

    public boolean revokeBan(ItemKey item){
        if (!restrictions.containsKey(item)) return false;
        restrictions.remove(item);
        save();
        return true;
    }

    public boolean removeMeta(ItemKey key){
        if (!restrictions.containsKey(key)) return false;
        Restriction restriction = restrictions.get(key);
        restrictions.remove(key);
        restrictions.put(key.getAll(), restriction);
        return true;
    }

    public Optional<Boolean> isBlackList(ItemKey key){
        if (!restrictions.containsKey(key)) return Optional.empty();
        else return Optional.of(restrictions.get(key).isDimsBlacklist());
    }

    public Optional<Boolean> toggleBlacklist(ItemKey key){
        if (!restrictions.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(restrictions.get(key).toggleBlacklist());
        save();
        return res;
    }

    public Optional<Boolean> addDim(ItemKey key, World world){
        if (!restrictions.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(restrictions.get(key).addDim(world));
        save();
        return res;
    }

    public Optional<Boolean> removeDim(ItemKey key, World world){
        if (!restrictions.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(restrictions.get(key).removeDim(world));
        save();
        return res;
    }

    public Optional<Restriction> getRestriction(ItemKey item){
        return Optional.ofNullable(restrictions.getOrDefault(item, null));
    }

    public Map<ItemKey, Restriction> getRestrictions(){
        return restrictions;
    }

}

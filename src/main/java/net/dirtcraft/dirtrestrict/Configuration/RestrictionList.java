package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class RestrictionList extends ConfigurationBase<Map<ItemKey, Restriction>>{
    @SuppressWarnings({"UnstableApiUsage"})
    public RestrictionList (DirtRestrict plugin){
        super(plugin, "Restrictions", new TypeToken<Map<ItemKey, Restriction>>(){}, new HashMap<>());
    }

    public boolean addBan(ItemKey item, String reason){
        if (config.containsKey(item)) return false;
        if (reason != null) config.put(item, new Restriction(reason));
        else config.put(item, new Restriction());
        save();
        return true;
    }

    public Optional<Boolean> toggleBanType(ItemKey item, RestrictionTypes type){
        Restriction restriction = config.get(item);
        if (restriction == null) return Optional.empty();
        final boolean val = restriction.toggleRestrictions(type);
        save();
        return Optional.of(val);
    }

    public boolean updateBanReason(ItemKey item, String reason){
        Restriction restriction = config.get(item);
        if (restriction == null) return false;
        restriction.setReason(reason);
        save();
        return true;
    }

    public boolean revokeBan(ItemKey item){
        if (!config.containsKey(item)) return false;
        config.remove(item);
        save();
        return true;
    }

    public boolean removeMeta(ItemKey key){
        if (!config.containsKey(key)) return false;
        Restriction restriction = config.get(key);
        config.remove(key);
        config.put(key.getAll(), restriction);
        return true;
    }

    public Optional<Boolean> isBlackList(ItemKey key){
        if (!config.containsKey(key)) return Optional.empty();
        else return Optional.of(config.get(key).isDimsBlacklist());
    }

    public Optional<Boolean> toggleBlacklist(ItemKey key){
        if (!config.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(config.get(key).toggleBlacklist());
        save();
        return res;
    }

    public Optional<Boolean> toggleHidden(ItemKey key){
        if (!config.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(config.get(key).toggleHidden());
        save();
        return res;
    }

    public Optional<Boolean> toggleRecipeDisabled(ItemKey key){
        if (!config.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(config.get(key).toggleRecipeDisabled(key));
        save();
        return res;
    }

    public Optional<Boolean> addDim(ItemKey key, World world){
        if (!config.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(config.get(key).addDim(world));
        save();
        return res;
    }

    public Optional<Boolean> removeDim(ItemKey key, World world){
        if (!config.containsKey(key)) return Optional.empty();
        Optional<Boolean> res = Optional.of(config.get(key).removeDim(world));
        save();
        return res;
    }

    public Optional<Restriction> getRestriction(ItemKey item){
        return Optional.ofNullable(config.getOrDefault(item, null));
    }

    public Map<ItemKey, Restriction> getRestrictions(){
        return config;
    }

}

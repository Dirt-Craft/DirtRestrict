package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.*;

@ConfigSerializable
public class Restriction {
    @Setting private boolean hidden;
    @Setting private boolean dimWhitelistMode;
    @Setting private String reason;
    @Setting private EnumSet<RestrictionTypes> restrictions;
    @Setting private HashSet<UUID> dims;

    public Restriction(){
        this.hidden = false;
        this.reason = "";
        this.restrictions = EnumSet.allOf(RestrictionTypes.class);
        this.dims = new HashSet<>();
    }

    public Restriction(String reason){
        this.hidden = false;
        this.reason = reason;
        this.restrictions = EnumSet.allOf(RestrictionTypes.class);
        this.dims = new HashSet<>();
    }

    public String getReason() {
        return reason;
    }

    public boolean isRestricted(RestrictionTypes type, @Nullable World world) {
        if (!restrictions.contains(type)) return false;
        if (world == null || dims.contains(world.getUID())) return !dimWhitelistMode;
        return false;
    }

    public boolean toggleRestrictions(RestrictionTypes type){
        final boolean value = restrictions.contains(type);
        if (value) restrictions.remove(type);
        else restrictions.add(type);
        return restrictions.contains(type);
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isDimsBlacklist(){
        return !dimWhitelistMode;
    }

    public boolean toggleBlacklist(){
        dimWhitelistMode = !dimWhitelistMode;
        return !dimWhitelistMode;
    }

    public boolean addDim(World world){
        if (world == null || dims.contains(world)) return false;
        dims.add(world.getUID());
        return true;
    }

    public boolean removeDim(World world){
        if (world == null || dims.contains(world)) return false;
        dims.remove(world.getUID());
        return true;
    }

    public Collection<UUID> getDims(){
        return dims;
    }
}

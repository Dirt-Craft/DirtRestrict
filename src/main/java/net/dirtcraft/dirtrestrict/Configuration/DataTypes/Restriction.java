package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.*;

@ConfigSerializable
public class Restriction {
    @Setting private boolean hidden;
    @Setting private String reason;
    @Setting private EnumSet<RestrictionTypes> restrictions;
    @Setting private HashSet<String> dims;

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
    }

    public String getReason() {
        return reason;
    }

    public boolean isRestricted(RestrictionTypes type, @Nullable World world) {
        return restrictions.contains(type) && (world == null || dims.isEmpty() || dims.contains(world.getUID()));
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
}

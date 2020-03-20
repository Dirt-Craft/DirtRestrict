package net.dirtcraft.dirtrestrict.Configuration;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
@ConfigSerializable
public class Restriction {
    @Setting private String itemType;
    @Setting private int itemMeta;
    @Setting private String reason;
    @Setting private List<RestrictionType> restrictions;

    public Restriction(ItemStack item, String reason, RestrictionType... types){
        this.itemType = item.getType().name();
        this.itemMeta = item.getDurability();
        this.reason = reason;
        this.restrictions = Arrays.asList(types);
    }

    public String getItemType() {
        return itemType;
    }

    public int getItemMeta() {
        return itemMeta;
    }

    public String getReason() {
        return reason;
    }

    public String getIndex(){
        return itemType + RestrictionList.separator + itemMeta;
    }

    public boolean isRestricted(RestrictionType type) {
        return restrictions.contains(type);
    }

    public void setRestrictions(RestrictionType type, boolean value) {
        if (value) this.restrictions.add(type);
        else this.restrictions.remove(type);
    }

    public void toggleRestrictions(RestrictionType type){
        setRestrictions(type, !isRestricted(type));
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

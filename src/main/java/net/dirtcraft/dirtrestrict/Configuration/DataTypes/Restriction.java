package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.RecipeHelper;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.UUID;

@ConfigSerializable
public class Restriction {
    @Setting private EnumSet<RestrictionTypes> restrictions;
    @Setting private HashSet<UUID> dims;
    @Setting private String reason;
    @Setting private boolean dimBlacklist;
    @Setting private boolean hidden;
    @Setting private boolean recipeDisabled;
    @Setting private boolean worldScan;

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
        if (world == null) return !dimBlacklist;
        if (dimBlacklist) return dims.contains(world.getUID());
        else return !dims.contains(world.getUID());
    }

    public boolean isRestricted(RestrictionTypes type) {
        return (!restrictions.contains(type));
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
        return dimBlacklist;
    }

    public boolean toggleBlacklist(){
        dimBlacklist = !dimBlacklist;
        return dimBlacklist;
    }

    public boolean addDim(World world){
        if (world == null || dims.contains(world.getUID())) return false;
        dims.add(world.getUID());
        return true;
    }

    public boolean removeDim(World world){
        if (world == null || dims.contains(world.getUID())) return false;
        dims.remove(world.getUID());
        return true;
    }

    public Collection<UUID> getDims(){
        return dims;
    }

    public boolean hasDim(UUID uuid){
        return dims.contains(uuid);
    }

    public boolean isRecipeDisabled() {
        return recipeDisabled;
    }

    public boolean toggleRecipeDisabled(ItemKey itemKey){
        recipeDisabled = !recipeDisabled;
        RecipeHelper recipeHelper = DirtRestrict.getInstance().getRecipeHelper();
        if (recipeDisabled){
            recipeHelper.removeCraftingRecipe(itemKey);
            recipeHelper.removeSmeltingRecipe(itemKey);
        } else {
            recipeHelper.restoreCraftingRecipe(itemKey);
            recipeHelper.restoreSmeltingRecipe(itemKey);
        }
        return recipeDisabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean toggleHidden(){
        hidden = !hidden;
        return hidden;
    }

    public boolean isWorldScan() {
        return worldScan;
    }

    public boolean toggleWorldScan(){
        worldScan = !worldScan;
        return worldScan;
    }
}

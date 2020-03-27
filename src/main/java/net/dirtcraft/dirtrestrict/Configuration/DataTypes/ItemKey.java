package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.minecraft.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import scala.tools.reflect.FormatInterpolator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ItemKey {
    private static final DirtRestrict dirtRestrict = DirtRestrict.getInstance();
    public final int item;
    public final Byte data;

    public ItemKey(MaterialData data){
        this.item = data.getItemTypeId();
        this.data = data.getData();
    }

    public ItemKey(Material material){
        this.item = material.getId();
        this.data = null;
    }

    public ItemKey(Block block){
        this.item = block.getTypeId();
        this.data = block.getState().getData().getData();
    }

    public ItemKey(Item item, Byte b){
        this.item = Item.getIdFromItem(item);
        this.data = b;
    }

    public ItemKey(Material material, Byte b){
        this.item = material.getId();
        this.data = b;
    }

    public ItemKey(ItemStack itemStack, boolean data){
        this.item = itemStack.getType().getId();
        this.data = data ? itemStack.getData().getData() : null;
    }

    public ItemKey(int item, Byte b){
        this.item = item;
        this.data = b;
    }

    public String getName(){
        final byte data = this.data == null? 0 : this.data;
        return new net.minecraft.item.ItemStack(Item.getItemById(item), 0, data).getDisplayName();
    }

    public String getId(){
        return data == null ? String.valueOf(item) : item + ":" + data;
    }

    public Item getItem(){
        return Item.getItemById(item);
    }

    public String getUniqueIdentifier(){
        final Item item = Item.getItemById(this.item);
        final GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
        String itemID = id.modId + ":" + id.name;
        return data != null? itemID + ":" + data : itemID;
    }

    public Material getMaterial(){
        return Material.getMaterial(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKey itemKey = (ItemKey) o;
        return item == itemKey.item && Objects.equals(data, itemKey.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, data);
    }

    public ItemKey getAll(){
        return new ItemKey(item, null);
    }

    public Optional<Restriction> hasPermission(@Nullable Player player, @Nonnull RestrictionTypes type, @Nullable Location location){
        return isRestricted(type, location).flatMap(r->checkPerms(player,type,location,r));
    }

    private Optional<Restriction> isRestricted(RestrictionTypes type, @Nullable Location location){
        final World world = location == null ? null : location.getWorld();
        Optional<Restriction> optRestriction = dirtRestrict.getRestrictions().getRestriction(this);
        if (!optRestriction.isPresent()) optRestriction = dirtRestrict.getRestrictions().getRestriction(getAll());
        if (optRestriction.isPresent() && optRestriction.get().isRestricted(type, world)) return optRestriction;
        else return Optional.empty();
    }

    private Optional<Restriction> checkPerms(Player player, RestrictionTypes type, Location location, Restriction restriction){
        final String itemId = String.valueOf(item);
        final String meta = data == null? "*" : String.valueOf(data);
        final String sType = type.getName();
        final String world = location == null? "*" : location.getWorld() == null? "*" : location.getWorld().getName();
        final boolean bypass = checkPerms(player, itemId, meta, sType, world);
        if (bypass) return Optional.empty();
        else return Optional.of(restriction);
    }

    private boolean checkPerms(Player player, String itemId, String meta, String type, String world){
        if (player.hasPermission(Permission.PERMISSION_ADMIN)) return true;
        if (checkPerm(player, itemId, meta, type, world)) return true;
        if (checkPerm(player, itemId, "*", type, world)) return true;
        if (checkPerm(player, itemId, meta, "*", world)) return true;
        if (checkPerm(player, itemId, "*", "*", world)) return true;
        return false;
    }

    private boolean checkPerm(Player player, String... check){
        StringBuilder sb = new StringBuilder("dirtrestrict.bypass");
        Arrays.stream(check).forEach(s->sb.append(".").append(s));
        return player.hasPermission(sb.toString());
    }
}

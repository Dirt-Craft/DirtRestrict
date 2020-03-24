package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Objects;

public class ItemKey {
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
}

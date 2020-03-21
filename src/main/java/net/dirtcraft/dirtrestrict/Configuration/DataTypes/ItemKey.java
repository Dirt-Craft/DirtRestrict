package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Objects;

public class ItemKey {
    public final Material material;
    public final Byte data;

    public ItemKey(MaterialData data){
        this.material = data.getItemType();
        this.data = data.getData();
    }

    public ItemKey(Material material){
        this.material = material;
        this.data = null;
    }

    public ItemKey(Material material, Byte b){
        this.material = material;
        this.data = b;
    }

    public ItemKey(ItemStack itemStack, boolean data){
        this.material = itemStack.getType();
        this.data = data ? itemStack.getData().getData() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemKey itemKey = (ItemKey) o;
        return material == itemKey.material && Objects.equals(data, itemKey.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, data);
    }
}

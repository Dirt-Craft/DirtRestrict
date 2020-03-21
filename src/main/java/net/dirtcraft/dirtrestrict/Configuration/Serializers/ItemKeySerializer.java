package net.dirtcraft.dirtrestrict.Configuration.Serializers;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemKeySerializer implements TypeSerializer<ItemKey> {
    @Nullable
    @Override
    public ItemKey deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        final String key = value.getString();
        final boolean hasMeta = key.matches("^.+:\\d+$");
        if (hasMeta){
            final String[] splitKey = key.split(":");
            final Material material = Material.getMaterial(splitKey[0]);
            final Byte b = Byte.parseByte(splitKey[1]);
            return new ItemKey(new MaterialData(material, b));
        } else {
            final Material material = Material.getMaterial(key);
            return new ItemKey(material);
        }
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ItemKey obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (obj == null || obj.material == null) return;
        if (obj.data != null) value.setValue(obj.material + ":" + obj.data);
        else value.setValue(obj.material.toString());
    }
}

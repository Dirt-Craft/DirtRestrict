package net.dirtcraft.dirtrestrict.Configuration.Serializers;

import com.google.common.reflect.TypeToken;
import cpw.mods.fml.common.registry.GameRegistry;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Utility.CommandUtils;
import net.minecraft.item.Item;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ItemKeySerializer implements TypeSerializer<ItemKey> {
    @Nullable
    @Override
    public ItemKey deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (value.getString() == null || value.getString().equalsIgnoreCase("")) return null;
        final String[] key = value.getString().split(":");
        final String modId = key[0];
        final Byte meta = CommandUtils.parseByte(key[key.length -1]).orElse(null);
        final String itemId;
        if ((key.length == 3 && meta != null) || (key.length == 2 && meta == null)){
            final String[] itemIdArr = new String[key.length - (meta == null ? 1 : 2)];
            System.arraycopy(key, 1, itemIdArr, 0, key.length - (meta == null? 1 : 2));
            itemId = String.join(":", itemIdArr);
        } else {
            itemId = key[1];
        }
        Item item = GameRegistry.findItem(modId, itemId);
        return new ItemKey(item, meta);

    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable ItemKey obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (obj == null || obj.item == 0 || Item.getItemById(obj.item) == null) return;
        else value.setValue(obj.getUniqueIdentifier());
    }
}

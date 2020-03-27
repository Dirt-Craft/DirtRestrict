package net.dirtcraft.dirtrestrict.Configuration.Serializers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class UUIDSerializer implements TypeSerializer<UUID> {
    @Nullable
    @Override
    public UUID deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (value.getString() == null || value.getString().equalsIgnoreCase("")) return null;
        return UUID.fromString(value.getString());
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable UUID obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (obj == null) return;
        value.setValue(obj.toString());
    }
}

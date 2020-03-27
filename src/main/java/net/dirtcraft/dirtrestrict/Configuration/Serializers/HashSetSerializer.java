package net.dirtcraft.dirtrestrict.Configuration.Serializers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

@SuppressWarnings({"UnstableApiUsage"})
public class HashSetSerializer implements TypeSerializer<HashSet<?>> {
    @Nullable
    @Override
    public HashSet<?> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        return new HashSet<>((Collection<?>) value.getList(type.resolveType(HashSet.class.getTypeParameters()[0])));
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable HashSet<?> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (obj == null) return;
        value.setValue(new ArrayList<>(obj));
    }

}

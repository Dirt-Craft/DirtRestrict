package net.dirtcraft.dirtrestrict.Configuration.Serializers;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;

@SuppressWarnings({"UnstableApiUsage", "unchecked", "ConstantConditions", "rawtypes"})
public class HashSetSerializer implements TypeSerializer<HashSet<?>> {
    @Nullable
    @Override
    public HashSet<?> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        return new HashSet<>((Collection<?>) value.getList(type.resolveType(HashSet.class.getTypeParameters()[0])));
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable HashSet<?> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        if (!(type.getType() instanceof ParameterizedType)) {
            throw new ObjectMappingException("Raw types are not supported for collections");
        }
        TypeToken<?> entryType = type.resolveType(HashSet.class.getTypeParameters()[0]);
        TypeSerializer entrySerial = value.getOptions().getSerializers().get(entryType);
        if (entrySerial == null) {
            throw new ObjectMappingException("No applicable type serializer for type " + entryType);
        }
        value.setValue(ImmutableList.of());
        for (Object ent : obj) {
            entrySerial.serialize(entryType, ent, value.getAppendedNode());
        }
    }

}

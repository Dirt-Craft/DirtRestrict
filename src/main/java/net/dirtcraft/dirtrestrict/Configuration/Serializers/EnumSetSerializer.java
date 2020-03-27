package net.dirtcraft.dirtrestrict.Configuration.Serializers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
@SuppressWarnings({"UnstableApiUsage", "unchecked", "ConstantConditions"})
public class EnumSetSerializer<T extends Enum<T>> implements TypeSerializer<EnumSet<T>> {
    @Nullable
    @Override
    public EnumSet<T> deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value) throws ObjectMappingException {
        Class<T> literal = (Class<T>) type.resolveType(EnumSet.class.getTypeParameters()[0]).getRawType();
        List<String> enumeration =  value.getList(TypeToken.of(String.class), new ArrayList<>());
        List<T> enums = new ArrayList<>();
        enumeration.forEach(s->{
            try{
                enums.add(T.valueOf(literal, s));
            } catch (EnumConstantNotPresentException e){
                e.printStackTrace();
            }
        });
        return EnumSet.copyOf(enums);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable EnumSet<T> obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
        value.setValue(new TypeToken<List<String>>(){}, obj.stream().map(T::toString).collect(Collectors.toList()));
    }
}

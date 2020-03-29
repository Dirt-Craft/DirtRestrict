package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.BypassSettings;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.EnumSetSerializer;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.UUIDSerializer;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
public abstract class ConfigurationBase<T> {
    protected final DirtRestrict plugin;
    private final AtomicBoolean isDirty = new AtomicBoolean();
    private final AtomicBoolean saving = new AtomicBoolean();
    private final HoconConfigurationLoader loader;
    private final TypeToken<T> token;
    private ConfigurationNode node;
    protected T preferences;

    static {
        //final TypeSerializerCollection serializers = TypeSerializers.getDefaultSerializers();
        //serializers.registerType(new TypeToken<EnumSet<RestrictionTypes>>(){}, new EnumSetSerializer<>());
        //serializers.registerType(new TypeToken<UUID>(){}, new UUIDSerializer());
    }

    public ConfigurationBase (DirtRestrict plugin, String file, TypeToken<T> token, T instance){
        this.plugin = plugin;
        this.token = token;
        final File loc = new File(plugin.getDataFolder(), file + ".hocon");
        plugin.getDataFolder().mkdirs();
        loader = HoconConfigurationLoader.builder()
                .setFile(loc)
                .build();
        try{
            node = loader.load(loader.getDefaultOptions().setShouldCopyDefaults(true));
            preferences = node.getValue(token, instance);
            loader.save(node);
        } catch (IOException | ObjectMappingException e){
            plugin.getLogger().log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

    public void save(){
        isDirty.set(true);
        if (saving.getAndSet(true)) return;
        CompletableFuture.supplyAsync(()->{
            while (true) {
                try {
                    node.setValue(token, preferences);
                    loader.save(node);
                    isDirty.set(false);
                    Thread.sleep(5000);
                    if (!isDirty.get()){
                        saving.set(false);
                        return true;
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, e.getMessage());
                    e.printStackTrace();
                    saving.set(false);
                    return false;
                }
            }
        });
    }
}

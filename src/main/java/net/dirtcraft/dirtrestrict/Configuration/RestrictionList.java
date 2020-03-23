package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.Serializers.ItemKeySerializer;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.minecraft.item.ItemStack;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public class RestrictionList {
    private final DirtRestrict plugin;
    private final AtomicBoolean isDirty = new AtomicBoolean();
    private final AtomicBoolean saving = new AtomicBoolean();
    private final HoconConfigurationLoader loader;
    private ConfigurationNode node;
    private Map<ItemKey, Restriction> restrictions;

    public RestrictionList (DirtRestrict plugin){
        this.plugin = plugin;
        final File loc = new File(plugin.getDataFolder(), "Restrictions.hocon");
        plugin.getDataFolder().mkdirs();
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(ItemKey.class), new ItemKeySerializer());
        loader = HoconConfigurationLoader.builder()
                .setFile(loc)
                .build();
        try{
            node = loader.load(loader.getDefaultOptions().setShouldCopyDefaults(true));
            restrictions = node.getValue(new TypeToken<Map<ItemKey, Restriction>>(){}, new HashMap<>());
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
                    node.setValue(new TypeToken<Map<ItemKey, Restriction>>() {}, restrictions);
                    loader.save(node);
                    isDirty.set(false);
                    Thread.sleep(5000);
                    if (!isDirty.get()){
                        saving.set(false);
                        return true;
                    }
                } catch (IOException | ObjectMappingException | InterruptedException e) {
                    plugin.getLogger().log(Level.SEVERE, e.getMessage());
                    e.printStackTrace();
                    saving.set(false);
                    return false;
                }
            }
        });
    }

    public boolean addBan(ItemKey item){
        if (restrictions.containsKey(item)) return false;
        restrictions.put(item, new Restriction());
        save();
        return true;
    }

    public Optional<Boolean> toggleBanType(ItemKey item, RestrictionTypes type){
        Restriction restriction = restrictions.get(item);
        if (restriction == null) return Optional.empty();
        final boolean val = restriction.toggleRestrictions(type);
        save();
        return Optional.of(val);
    }

    public boolean updateBanReason(ItemKey item, String reason){
        Restriction restriction = restrictions.get(item);
        if (restriction == null) return false;
        restriction.setReason(reason);
        save();
        return true;
    }

    public boolean revokeBan(ItemKey item){
        if (!restrictions.containsKey(item)) return false;
        restrictions.remove(item);
        save();
        return true;
    }

    public Optional<Restriction> getRestriction(ItemKey item){
        return Optional.ofNullable(restrictions.getOrDefault(item, null));
    }

    public Map<ItemKey, Restriction> getRestrictions(){
        return restrictions;
    }

    public boolean isRestricted(Player player, MaterialData item, RestrictionTypes type){
        final int itemId = item.getItemTypeId();
        final byte meta = item.getData();
        return isRestricted(player, itemId, meta, type);
    }

    public boolean isRestricted(Player player, Block block, RestrictionTypes type){
        final int itemId = block.getTypeId();
        final byte meta = block.getState().getData().getData();
        return isRestricted(player, itemId, meta, type);
    }

    private boolean isRestricted(Player player, int itemId, Byte meta, RestrictionTypes type){
        final ItemKey key = new ItemKey(itemId, meta);
        System.out.println(itemId + ":" + meta);

        Optional<Restriction> optRestriction = getRestriction(key);
        if (!optRestriction.isPresent()) optRestriction = getRestriction(new ItemKey(itemId, null));
        return !optRestriction.isPresent() || optRestriction.get().isRestricted(type) || hasPermission(player, key, type);
    }

    private boolean hasPermission(Player player, ItemKey itemKey, RestrictionTypes type){
        return checkPerms(player, itemKey.getUniqueIdentifier(), String.valueOf(itemKey.data), type.toString().toLowerCase());
    }

    private boolean checkPerms(Player player, String itemId, String meta, String type){
        if (checkPerm(player, "*", meta, type)) return true;
        if (checkPerm(player, itemId, "*", type)) return true;
        if (checkPerm(player, itemId, meta, type)) return true;
        return false;
    }

    private boolean checkPerm(Player player, String... check){
        StringBuilder sb = new StringBuilder("dirtrestrict.bypass.");
        Arrays.stream(check).forEach(sb::append);
        return player.hasPermission(sb.toString());
    }

}

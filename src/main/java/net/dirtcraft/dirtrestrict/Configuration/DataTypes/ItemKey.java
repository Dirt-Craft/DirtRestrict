package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import cpw.mods.fml.common.registry.GameRegistry;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.item.Item;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static net.dirtcraft.dirtrestrict.Configuration.Permission.BYPASS_BASE;
import static net.dirtcraft.dirtrestrict.Configuration.Permission.PERMISSION_ADMIN;

public class ItemKey implements Comparable<ItemKey> {
    private static final DirtRestrict dirtRestrict = DirtRestrict.getInstance();
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
        final byte data = this.data == null? 0 : this.data;
        return new net.minecraft.item.ItemStack(Item.getItemById(item), 0, data).getDisplayName();
    }

    public int getId(){
        return item;
    }

    public String getMeta(){
        return data == null? "*" : data.toString();
    }

    public Item getItem(){
        return Item.getItemById(item);
    }

    public String getUniqueIdentifier(){
        return getUniqueIdentifier(true);
    }

    public String getUniqueIdentifier(boolean meta){
        final Item item = Item.getItemById(this.item);
        final GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(item);
        String itemID = id.modId + ":" + id.name;
        if (!meta || data == null) return itemID;
        else return itemID + ":" + data;
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

    public Optional<Restriction> isRestricted(@Nullable Player player, @Nonnull RestrictionTypes type, @Nullable Location location){
        final World world = location == null ? null : location.getWorld();
        Optional<Restriction> restriction = dirtRestrict.getRestrictions().getRestriction(this);
        if (!restriction.isPresent()) restriction = dirtRestrict.getRestrictions().getRestriction(getAll());
        if (!restriction.isPresent() || !restriction.get().isRestricted(type, world)) return Optional.empty();
        if (player == null || !checkPerms(player, type, location, restriction.get())) return restriction;
        else return Optional.empty();
    }

    private boolean checkPerms(Player player, RestrictionTypes rawType, Location location, Restriction restriction){
        final String itemId = getUniqueIdentifier(false).replace(":", ".");
        final String meta = data == null? "*" : String.valueOf(data);
        final String type = rawType.getName();
        final String world = location == null? "*" : location.getWorld() == null? "*" : location.getWorld().getName();
        final Permission fullyQualified = getPermissionNode(itemId, meta, type, world);
        boolean notify = false;
        Sound sound = Sound.ZOMBIE_METAL;
        if (player.hasPermission(PERMISSION_ADMIN)) {
            final AdminProfile profile = DirtRestrict.getInstance().getPreferences().getPreferences(player);
            final boolean verbose = profile.isShowPermissionNodes();
            final BypassSettings setting = profile.getBypassSetting();
            if (verbose) {
                TextComponent permissionNode = new TextComponent("§6§l[§cDirt§fRestrict§6§l] §dVerbose: §a" + fullyQualified.getName());
                permissionNode.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, fullyQualified.getName()));
                permissionNode.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.getMono("§3§nClick to copy node.")));
                player.spigot().sendMessage(permissionNode);
            }
            if (setting == BypassSettings.IGNORE) {
                return true;
            } else if (setting == BypassSettings.NOTIFY) {
                notify = true;
                sound = profile.getSound();
            }
        }

        if (player.hasPermission(fullyQualified)) return true;
        if (player.hasPermission(getPermissionNode(itemId, "*", type, world))) return true;
        if (player.hasPermission(getPermissionNode(itemId, meta, "*", world))) return true;
        if (player.hasPermission(getPermissionNode(itemId, "*", "*", world))) return true;
        if (notify){
            dirtRestrict.getSoundHandler().sendAdminNotification(player, sound);
            return true;
        }
        return false;
    }

    private Permission getPermissionNode(String... check){
        StringBuilder sb = new StringBuilder(BYPASS_BASE);
        Arrays.stream(check).forEach(s->sb.append(".").append(s));
        return new Permission(sb.toString(), PermissionDefault.FALSE);
    }

    @Override
    public int compareTo(ItemKey o) {
        if (o.item < item) return 1;
        else if (o.item > item) return -1;
        if (data == null || o.data == null){
            if (data == null && o.data == null) return 0;
            if (data == null) return 1;
            return -1;
        }
        if (o.data < data) return 1;
        else if (o.data > data) return -1;
        return 0;
    }
}

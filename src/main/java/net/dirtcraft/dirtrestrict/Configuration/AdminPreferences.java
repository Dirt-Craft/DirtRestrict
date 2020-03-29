package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.*;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class AdminPreferences extends ConfigurationBase<HashMap<UUID, AdminProfile>> implements Listener {

    @SuppressWarnings("UnstableApiUsage")
    public AdminPreferences(DirtRestrict plugin) {
        super(plugin, "Preferences", new TypeToken<HashMap<UUID, AdminProfile>>(){}, new HashMap<>());
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event){
        if (event.getPlayer() == null || !preferences.containsKey(event.getPlayer().getUniqueId()) || event.getPlayer().hasPermission(Permission.PERMISSION_ADMIN)) return;
        preferences.remove(event.getPlayer().getUniqueId());
    }

    public AdminProfile getPreferences(Player player){
        UUID uuid = player.getUniqueId();
        if (!preferences.containsKey(uuid)) preferences.put(uuid, new AdminProfile());
        return preferences.get(uuid);
    }

    public boolean setBypassSettings(Player player, BypassSettings setting){
        UUID uuid = player.getUniqueId();
        if (!preferences.containsKey(uuid)) return false;
        preferences.get(uuid).setBypassSetting(setting);
        return true;
    }

    public boolean setVerbose(Player player, boolean setting){
        UUID uuid = player.getUniqueId();
        if (!preferences.containsKey(uuid)) return false;
        preferences.get(uuid).setShowPermissionNodes(setting);
        return true;
    }



}

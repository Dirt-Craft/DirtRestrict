package net.dirtcraft.dirtrestrict.Configuration;

import com.google.common.reflect.TypeToken;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.AdminProfile;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.BypassSettings;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class AdminPreferences extends ConfigurationBase<HashMap<UUID, AdminProfile>> implements Listener {

    @SuppressWarnings("UnstableApiUsage")
    public AdminPreferences(DirtRestrict plugin) {
        super(plugin, "Preferences", new TypeToken<HashMap<UUID, AdminProfile>>(){}, new HashMap<>());
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event){
        if (event.getPlayer() == null || !config.containsKey(event.getPlayer().getUniqueId()) || event.getPlayer().hasPermission(Permission.PERMISSION_ADMIN)) return;
        config.remove(event.getPlayer().getUniqueId());
        save();
    }

    public AdminProfile getPreferences(Player player){
        UUID uuid = player.getUniqueId();
        if (!config.containsKey(uuid)) config.put(uuid, new AdminProfile());
        save();
        return config.get(uuid);
    }

    public boolean setBypassSettings(Player player, BypassSettings setting){
        UUID uuid = player.getUniqueId();
        if (!config.containsKey(uuid)) return false;
        config.get(uuid).setBypassSetting(setting);
        save();
        return true;
    }

    public boolean setVerbose(Player player, boolean setting){
        UUID uuid = player.getUniqueId();
        if (!config.containsKey(uuid)) return false;
        config.get(uuid).setShowPermissionNodes(setting);
        save();
        return true;
    }

    public boolean setSound(Player player, Sound sound) {
        UUID uuid = player.getUniqueId();
        if (!config.containsKey(uuid)) return false;
        config.get(uuid).setSound(sound);
        save();
        return true;
    }



}

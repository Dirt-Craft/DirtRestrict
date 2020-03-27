package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Optional;

public class DropListener extends RestrictionHandler {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        Location location = event.getItemDrop().getLocation();
        ItemKey itemKey = new ItemKey(event.getItemDrop().getItemStack().getData());
        RestrictionTypes type = RestrictionTypes.OWN;
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, type, location);

        if (bannedInfo.isPresent()) return;
        type = RestrictionTypes.DROP;
        bannedInfo = itemKey.hasPermission(p, type, location);

        if (!bannedInfo.isPresent()) return;
        event.setCancelled(true);
        soundHandler.sendPlingSound(p);
        printMessage(p, type, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
    }
}

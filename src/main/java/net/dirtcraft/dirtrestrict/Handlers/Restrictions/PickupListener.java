package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;

public class PickupListener extends RestrictionHandler {
    private Random random = new Random();

    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemPickup(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        ItemKey itemKey = new ItemKey(item.getData());
        RestrictionTypes type = RestrictionTypes.OWN;
        Optional<Restriction> bannedInfo = isRestricted(itemKey, type);

        if (bannedInfo.isPresent()) return;

        type = RestrictionTypes.PICKUP;
        bannedInfo = isRestricted(itemKey, type);

        if (!bannedInfo.isPresent()) return;
        event.setCancelled(true);

        Location loc = event.getItem().getLocation();
        event.getItem().teleport(new Location(loc.getWorld(), loc.getX() + getRandomInt(), loc.getY() + getRandomInt(), loc.getZ() + getRandomInt()));

        soundHandler.sendPlingSound(p);
        printMessage(p, type, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
    }

    private int getRandomInt() {
        return random.nextInt(5);
    }
}

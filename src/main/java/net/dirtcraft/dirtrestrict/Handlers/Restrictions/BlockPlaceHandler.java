package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public class BlockPlaceHandler extends RestrictionHandler {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlace(BlockPlaceEvent event) {
        if (event.getItemInHand() == null) return;
        final ItemKey itemKey = new ItemKey(event.getItemInHand().getData());
        final Optional<Restriction> bannedInfo = isRestricted(itemKey, RestrictionTypes.PLACE);
        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            soundHandler.sendEndermanTeleportSound(event.getPlayer());
            printMessage(event.getPlayer(), RestrictionTypes.BREAK, itemKey, restricts.getRestriction(itemKey).map(Restriction::getReason).orElse(null));
        }
    }
}

package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.BaseHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;

public class BlockPlaceHandler extends BaseHandler implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlace(BlockPlaceEvent event) {
        final ItemKey itemKey = new ItemKey(event.getItemInHand().getData());
        final Optional<Restriction> bannedInfo = isRestricted(itemKey, RestrictionTypes.PLACE);
        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            soundHandler.sendEndermanTeleportSound(event.getPlayer());
            printMessage(event.getPlayer(), RestrictionTypes.BREAK, itemKey, restricts.getRestriction(itemKey).map(Restriction::getReason).orElse(null));
        }
    }
}

package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.BaseHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.MaterialData;

import java.util.Optional;

public class BlockBreakHandler extends BaseHandler implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {
        boolean compareDrops = false;
        MaterialData itemToDrop = null;
        if (event.getBlock().getDrops().iterator().hasNext()) {
            itemToDrop = event.getBlock().getDrops().iterator().next().getData();
            if (event.getBlock().getTypeId() == itemToDrop.getItemTypeId()) {
                compareDrops = true;
            }
        }
        final ItemKey itemKey = compareDrops ? new ItemKey(itemToDrop) : new ItemKey(event.getBlock());
        final Optional<Restriction> banned = isRestricted(itemKey, RestrictionTypes.BREAK);
        if (banned.isPresent()) {
            event.setCancelled(true);
            dirtRestrict.getSoundHandler().sendPlingSound(event.getPlayer());
            printMessage(event.getPlayer(), RestrictionTypes.BREAK, itemKey, banned.map(Restriction::getReason).orElse(null));
        }
    }
}

package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Iterator;
import java.util.Optional;

public class BlockBreakHandler extends RestrictionHandler {
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {
        final Iterator<ItemStack> itemIterator = event.getBlock().getDrops().iterator();
        boolean compareDrops = false;
        MaterialData itemToDrop = null;
        if (itemIterator.hasNext()) {
            ItemStack itemStack = itemIterator.next();
            itemToDrop = itemStack.getData();
            if (event.getBlock().getTypeId() == itemToDrop.getItemTypeId()) {
                compareDrops = true;
            }
        }
        final ItemKey itemKey = compareDrops ? new ItemKey(itemToDrop) : new ItemKey(event.getBlock());
        final Optional<Restriction> banned = itemKey.hasPermission(event.getPlayer(), RestrictionTypes.BREAK, event.getBlock().getLocation());
        if (banned.isPresent()) {
            event.setCancelled(true);
            dirtRestrict.getSoundHandler().sendPlingSound(event.getPlayer());
            printMessage(event.getPlayer(), RestrictionTypes.BREAK, itemKey, banned.map(Restriction::getReason).orElse(null));
        }
    }
}

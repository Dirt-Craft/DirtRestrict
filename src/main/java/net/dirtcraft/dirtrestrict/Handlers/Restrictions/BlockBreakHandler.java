package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.BaseHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakHandler extends BaseHandler implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event) {
            boolean compareDrops = false;
            ItemStack itemToDrop = null;
            if (event.getBlock().getDrops().iterator().hasNext()) {
                itemToDrop = event.getBlock().getDrops().iterator().next();
                if (event.getBlock().getTypeId() == itemToDrop.getTypeId()) {
                    compareDrops = true;
                }
            }
            final boolean banned;
            if (!compareDrops) banned = restricts.isRestricted(event.getPlayer(), event.getBlock(), RestrictionTypes.BREAK);
            else banned = restricts.isRestricted(event.getPlayer(), itemToDrop.getData(), RestrictionTypes.BREAK);

            if (banned) {
                event.setCancelled(true);
                //ir.getSoundHandler().sendPlingSound(event.getPlayer());
            }
        }
}

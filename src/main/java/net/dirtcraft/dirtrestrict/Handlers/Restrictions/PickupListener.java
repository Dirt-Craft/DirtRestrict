package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.BaseHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PickupListener extends BaseHandler implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event){
        final Iterator<ItemStack> dropsIterator = event.getBlock().getDrops().iterator();
        final Map<MaterialData, Boolean> checked = new HashMap<>();
        while (dropsIterator.hasNext()){
            final boolean allowed;
            ItemStack is = dropsIterator.next();
            if (checked.containsKey(is.getData())) {
                allowed = checked.get(is.getData());
            } else {
                allowed = restricts.isRestricted(event.getPlayer(), is.getData(), RestrictionTypes.BREAK);
                checked.put(is.getData(), allowed);
            }
            if (!allowed) dropsIterator.remove();
        }
    }
}

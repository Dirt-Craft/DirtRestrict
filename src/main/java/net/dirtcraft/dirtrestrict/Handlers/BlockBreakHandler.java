package net.dirtcraft.dirtrestrict.Handlers;

import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class BlockBreakHandler implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent event){
        final DirtRestrict plugin = DirtRestrict.getInstance();
        final RestrictionList banlist = plugin.getRestrictions();
        final Iterator<ItemStack> dropsIterator = event.getBlock().getDrops().iterator();
        while (dropsIterator.hasNext()){

        }
    }
}

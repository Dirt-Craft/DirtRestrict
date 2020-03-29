package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class SmeltingHandler extends RestrictionHandler {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onItemCrafted(FurnaceSmeltEvent event) {
        ItemStack item = event.getSource();
        Furnace f = (Furnace) event.getBlock().getState();
        if (!f.getInventory().getViewers().isEmpty()) {
            Player p = (Player) f.getInventory().getViewers().get(0);

            ItemKey itemKey = new ItemKey(item.getData());
            Optional<Restriction> bannedInfo = itemKey.isRestricted(p, RestrictionTypes.SMELTING, event.getBlock().getLocation());
            if (bannedInfo.isPresent()) {
                event.setCancelled(true);

                soundHandler.sendPlingSound(p);
                printMessage(p, RestrictionTypes.SMELTING, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
            }
        } else {
            ItemKey itemKey = new ItemKey(item.getData());
            Optional<Restriction> bannedInfo = itemKey.isRestricted(null, RestrictionTypes.SMELTING, event.getBlock().getLocation());
            if (bannedInfo.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

}

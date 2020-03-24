package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.BaseHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.Optional;

public class CraftingHandler extends BaseHandler implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onItemCrafted(PrepareItemCraftEvent event) {
        if (event.getRecipe() != null) {
            ItemKey itemKey = new ItemKey(event.getRecipe().getResult().getData());
            if (!event.getViewers().isEmpty()) {
                Player p = (Player) event.getViewers().get(0);
                Optional<Restriction> bannedInfo = isRestricted(itemKey, RestrictionTypes.CRAFTING);
                if (bannedInfo.isPresent()) {
                    event.getInventory().setResult(null);
                    soundHandler.sendPlingSound(p);
                    printMessage(p, RestrictionTypes.CRAFTING, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
                }
            } else {
                Optional<Restriction> bannedInfo = isRestricted(itemKey, RestrictionTypes.CRAFTING);
                if (bannedInfo.isPresent()) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }
}

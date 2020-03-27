package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.Optional;

public class CraftingHandler extends RestrictionHandler {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onItemCrafted(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        ItemKey itemKey = new ItemKey(event.getRecipe().getResult().getData());
        if (!event.getViewers().isEmpty()) {
            Player p = (Player) event.getViewers().get(0);
            Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.CRAFTING, p.getLocation());
            if (bannedInfo.isPresent()) {
                event.getInventory().setResult(null);
                soundHandler.sendPlingSound(p);
                printMessage(p, RestrictionTypes.CRAFTING, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
            }
        } else {
            Optional<Restriction> bannedInfo = itemKey.hasPermission(null, RestrictionTypes.CRAFTING, null);
            if (bannedInfo.isPresent()) {
                event.getInventory().setResult(null);
            }
        }
    }
}

package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class CreativeHandler extends RestrictionHandler {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreativeEvents(InventoryCreativeEvent event) {
        ItemStack cursorItem = event.getCursor();

        if (cursorItem == null || !(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        ItemKey itemKey = new ItemKey(cursorItem.getData());
        RestrictionTypes type = RestrictionTypes.OWN;
        Optional<Restriction> bannedInfo = isRestricted(itemKey, type);
        if (!bannedInfo.isPresent()) {
            type = RestrictionTypes.CREATIVE;
            bannedInfo = isRestricted(itemKey, type);
        }

        if (!bannedInfo.isPresent()) return;
        event.setCancelled(true);
        event.setCursor(null);

        soundHandler.sendItemBreakSound(p);
        printMessage(p, type, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemClicked(InventoryClickEvent event) {
        if (event.getSlotType() == null || event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        if (p.getGameMode() != GameMode.CREATIVE) return;

        ItemKey itemKey = new ItemKey(event.getCurrentItem().getData());
        RestrictionTypes type = RestrictionTypes.OWN;
        Optional<Restriction> bannedInfo = isRestricted(itemKey, type);
        if (bannedInfo.isPresent()) {
            type = RestrictionTypes.CREATIVE;
            bannedInfo = isRestricted(itemKey, type);
        }

        if (!bannedInfo.isPresent()) return;
        event.setCancelled(true);
        soundHandler.sendItemBreakSound(p);
        printMessage(p, type, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == null || event.getPlayer().getGameMode() != GameMode.CREATIVE) return;
        Player p = event.getPlayer();

        ItemStack item = p.getItemInHand();
        ItemKey itemKey = new ItemKey(item.getData());
        RestrictionTypes type = RestrictionTypes.OWN;
        Optional<Restriction> bannedInfo = isRestricted(itemKey, type);

        if (bannedInfo.isPresent()) {
            type = RestrictionTypes.OWN;
            bannedInfo = isRestricted(itemKey, type);
        }
        if (!bannedInfo.isPresent()) return;
        event.setCancelled(true);
        soundHandler.sendItemBreakSound(p);
        printMessage(p, type, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
    }
}

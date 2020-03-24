package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.BaseHandler;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Optional;

public class CreativeHandler extends BaseHandler implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreativeEvents(InventoryCreativeEvent event) {
        ItemStack cursorItem = event.getCursor();

        if (cursorItem == null) return;
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

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemClicked(InventoryClickEvent event) {
        if (event.getSlotType() != null) {
            if (event.getCurrentItem() != null) {
                Player p = (Player) event.getWhoClicked();
                if (p.getGameMode() == GameMode.CREATIVE) {
                    ItemStack currentItem = event.getCurrentItem();

                    MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, currentItem.getTypeId(), currentItem.getDurability(), p.getLocation());

                    if (bannedInfo == null) {
                        MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, currentItem.getTypeId(), currentItem.getDurability(), p.getLocation());

                        if (bannedInfo2 != null) {
                            event.setCancelled(true);

                            soundHandler.sendItemBreakSound(p);
                            ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
                        }
                    }
                }
            }
        }

    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (p.getGameMode() == GameMode.CREATIVE) {
            ItemStack item = p.getItemInHand();

            MaterialData bannedInfo = ir.getRestrictedItemsHandler().isBanned(ActionType.Ownership, p, item.getTypeId(), item.getDurability(), p.getLocation());

            if (bannedInfo == null) {
                MaterialData bannedInfo2 = ir.getRestrictedItemsHandler().isBanned(ActionType.Creative, p, item.getTypeId(), item.getDurability(), p.getLocation());

                if (bannedInfo2 != null) {
                    event.setCancelled(true);

                    soundHandler.sendItemBreakSound(p);
                    ir.getConfigHandler().printMessage(p, "chatMessages.creativeRestricted", bannedInfo2.reason);
                }
            }
        }
    }
}

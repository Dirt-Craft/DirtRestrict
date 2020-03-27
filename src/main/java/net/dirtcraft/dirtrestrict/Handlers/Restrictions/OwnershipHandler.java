package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

public class OwnershipHandler extends RestrictionHandler {
    //Inventory Click
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemClicked(InventoryClickEvent event) {
        if (event.getSlotType() != null && event.getCurrentItem() != null) {
            Player p = (Player) event.getWhoClicked();
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();

            if (event.getInventory().getType() == InventoryType.PLAYER) {
                inventoryClickRestriction(event, currentItem, p, false);
            } else if (event.getRawSlot() >= event.getInventory().getSize()) {
                inventoryClickRestriction(event, cursorItem, p, true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemDrag(final InventoryDragEvent event) {
        Player p = (Player) event.getWhoClicked();
        ItemStack cursorItem = event.getOldCursor();

        if (cursorItem == null) return;
        ItemKey itemKey = new ItemKey(cursorItem.getData());
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

            if (bannedInfo.isPresent()) {
                event.setCancelled(true);
                p.getInventory().remove(cursorItem);
                soundHandler.sendItemBreakSound(p);
                printMessage(p, RestrictionTypes.OWN, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
            }

    }

    //Item Pickup
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemPickup(PlayerPickupItemEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();

        ItemKey itemKey = new ItemKey(item.getData());
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            event.getItem().remove();

            soundHandler.sendItemBreakSound(p);
            p.playEffect(event.getItem().getLocation(), Effect.MOBSPAWNER_FLAMES, 5);
            printMessage(p, RestrictionTypes.OWN, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }
    }

    //Switch hotbar slot
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemHeldSwitch(PlayerItemHeldEvent event) {
        int newSlot = event.getNewSlot();
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItem(newSlot);

        if (item == null) return;

        ItemKey itemKey = new ItemKey(item.getData());
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

        if (bannedInfo.isPresent()) {
            p.getInventory().setItem(newSlot, null);
            soundHandler.sendItemBreakSound(p);
            printMessage(p, RestrictionTypes.OWN, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }
    }

    //Creative event
    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreativeEvents(InventoryCreativeEvent event) {
        ItemStack cursorItem = event.getCursor();

        if (cursorItem == null) return;
        Player p = (Player) event.getWhoClicked();

        ItemKey itemKey = new ItemKey(cursorItem.getData());
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            event.setCursor(null);

            soundHandler.sendItemBreakSound(p);
            printMessage(p, RestrictionTypes.OWN, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }
    }

    //Interact event
    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();

        if (item == null) return;

        ItemKey itemKey = new ItemKey(item.getData());
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            p.setItemInHand(null);

            soundHandler.sendItemBreakSound(p);
            printMessage(p, RestrictionTypes.OWN, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }

    }

    //Item drop
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        ItemKey itemKey = new ItemKey(item.getData());
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

        if (bannedInfo.isPresent()) {
            event.getItemDrop().remove();
            p.setItemInHand(null);

            soundHandler.sendItemBreakSound(p);
            printMessage(p, RestrictionTypes.OWN, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }
    }

    private void inventoryClickRestriction(@Nonnull InventoryClickEvent event, @Nonnull ItemStack currentItem, @Nonnull Player p, boolean removeCursorItem) {
        ItemKey itemKey = new ItemKey(currentItem.getData());
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

        if (bannedInfo.isPresent()) {
            event.setCancelled(true);

            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                if (p.getInventory().getHelmet() != null) {
                    if (p.getInventory().getHelmet().isSimilar(currentItem)) {
                        p.getInventory().setHelmet(null);
                    }
                }
                if (p.getInventory().getChestplate() != null) {
                    if (p.getInventory().getChestplate().isSimilar(currentItem)) {
                        p.getInventory().setChestplate(null);
                    }
                }
                if (p.getInventory().getLeggings() != null) {
                    if (p.getInventory().getLeggings().isSimilar(currentItem)) {
                        p.getInventory().setLeggings(null);
                    }
                }
                if (p.getInventory().getBoots() != null) {
                    if (p.getInventory().getBoots().isSimilar(currentItem)) {
                        p.getInventory().setBoots(null);
                    }
                }
            } else if (removeCursorItem) {
                p.setItemOnCursor(null);
            } else {
                p.getInventory().remove(currentItem);
                event.getInventory().remove(currentItem);
            }

            soundHandler.sendItemBreakSound(p);
            printMessage(p, RestrictionTypes.OWN, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }
    }
}

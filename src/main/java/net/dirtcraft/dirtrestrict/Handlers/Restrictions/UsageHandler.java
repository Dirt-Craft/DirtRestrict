package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class UsageHandler extends RestrictionHandler {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();
        Block interactingBlock = event.getClickedBlock();

        if (interactingBlock == null) return;

        ItemKey itemKey = new ItemKey(interactingBlock);
        Optional<Restriction> bannedInfo = isRestricted(itemKey, RestrictionTypes.USE);

        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            soundHandler.sendPlingSound(p);
            printMessage(p, RestrictionTypes.USE, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        } else {
            if (!event.isBlockInHand()) {
                itemKey = new ItemKey(item.getData());
                bannedInfo = isRestricted(itemKey, RestrictionTypes.OWN);
                if (bannedInfo.isPresent()) return;

                bannedInfo = isRestricted(itemKey, RestrictionTypes.USE);
                if (!bannedInfo.isPresent()) return;

                event.setCancelled(true);
                Bukkit.getScheduler().runTask(dirtRestrict, () -> {
                    ItemStack handItem = p.getItemInHand();
                    p.getWorld().dropItem(p.getLocation(), handItem);
                    p.setItemInHand(null);
                    p.closeInventory();
                    p.updateInventory();
                });

                soundHandler.sendPlingSound(p);
                printMessage(p, RestrictionTypes.USE, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemHeldSwitch(PlayerItemHeldEvent event) {
        int newSlot = event.getNewSlot();
        Player p = event.getPlayer();
        ItemStack item = p.getInventory().getItem(newSlot);
        if (item == null || !item.getType().isBlock()) return;

        ItemKey itemKey = new ItemKey(item.getData());
        RestrictionTypes type = RestrictionTypes.OWN;
        Optional<Restriction> bannedInfo = isRestricted(itemKey, type);
        if (bannedInfo.isPresent()) return;
        type = RestrictionTypes.USE;
        bannedInfo = isRestricted(itemKey, type);

        if (bannedInfo.isPresent()) {
            p.getWorld().dropItem(p.getLocation(), item);
            p.getInventory().setItem(newSlot, null);
            p.updateInventory();

            soundHandler.sendPlingSound(p);
            printMessage(p, type, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && ((Player) event.getDamager()).getItemInHand() != null) {
            Player p = (Player) event.getDamager();
            ItemStack item = p.getItemInHand();

            ItemKey itemKey = new ItemKey(item.getData());
            Optional<Restriction> bannedInfo = isRestricted(itemKey, RestrictionTypes.USE);
            if (bannedInfo.isPresent()) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            Player p = (Player) ((Projectile)event.getDamager()).getShooter();
            ItemStack item = p.getItemInHand();

            ItemKey itemKey = new ItemKey(item.getData());
            Optional<Restriction> bannedInfo = isRestricted(itemKey, RestrictionTypes.USE);
            if (bannedInfo.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

}

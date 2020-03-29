package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.RestrictionHandler;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class UsageHandler extends RestrictionHandler {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onAccessInventory(InventoryOpenEvent event){
        if (!(event.getInventory().getHolder() instanceof BlockState) || !(event.getPlayer() instanceof Player)) return;
        final Player p = (Player) event.getPlayer();
        final BlockState src = (BlockState) event.getInventory().getHolder();
        final Block block = src.getBlock();
        if (block == null) return;
        ItemKey itemKey = new ItemKey(block);
        Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.USE, p.getLocation());
        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            soundHandler.sendPlingSound(p);
            printMessage(p, RestrictionTypes.USE, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        ItemStack item = p.getItemInHand();
        Block interactingBlock = event.getClickedBlock();
        ItemKey itemKey = null;
        Optional<Restriction> bannedInfo = Optional.empty();

        if (interactingBlock != null) {
            itemKey = new ItemKey(interactingBlock);
            bannedInfo = itemKey.hasPermission(p, RestrictionTypes.USE, p.getLocation());
        }

        if (bannedInfo.isPresent()) {
            event.setCancelled(true);
            soundHandler.sendPlingSound(p);
            printMessage(p, RestrictionTypes.USE, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
        } else {
            if (!event.isBlockInHand()) {
                itemKey = new ItemKey(item.getData());
                bannedInfo = itemKey.hasPermission(p, RestrictionTypes.OWN, p.getLocation());

                if (bannedInfo.isPresent()) return;
                bannedInfo = itemKey.hasPermission(p, RestrictionTypes.USE, p.getLocation());

                if (!bannedInfo.isPresent()) return;
                event.setCancelled(true);
                soundHandler.sendPlingSound(p);
                printMessage(p, RestrictionTypes.USE, itemKey, bannedInfo.map(Restriction::getReason).orElse(null));
            }
        }
    }

    /*
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
     */

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && ((Player) event.getDamager()).getItemInHand() != null) {
            Player p = (Player) event.getDamager();
            ItemStack item = p.getItemInHand();

            ItemKey itemKey = new ItemKey(item.getData());
            Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.USE, p.getLocation());
            if (bannedInfo.isPresent()) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            Player p = (Player) ((Projectile)event.getDamager()).getShooter();
            ItemStack item = p.getItemInHand();

            ItemKey itemKey = new ItemKey(item.getData());
            Optional<Restriction> bannedInfo = itemKey.hasPermission(p, RestrictionTypes.USE, p.getLocation());
            if (bannedInfo.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

}

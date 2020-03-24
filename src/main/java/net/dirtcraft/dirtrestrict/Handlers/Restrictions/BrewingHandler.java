package net.dirtcraft.dirtrestrict.Handlers.Restrictions;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Handlers.BaseHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BrewingHandler extends BaseHandler implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBrewingPotions(BrewEvent event) {
        //Get potions before brewing
        final ItemStack Slot0;
        final ItemStack Slot1;
        final ItemStack Slot2;
        final ItemStack ing = event.getContents().getIngredient();
        final ItemStack ingredient = new ItemStack(ing.getType(), 1, ing.getDurability());

        if (event.getContents().getItem(0) != null) {
            Slot0 = new ItemStack(event.getContents().getItem(0).getType(), 1, (short) event.getContents().getItem(0).getDurability());
        } else {
            Slot0 = null;
        }
        if (event.getContents().getItem(1) != null) {
            Slot1 = new ItemStack(event.getContents().getItem(1).getType(), 1, (short) event.getContents().getItem(1).getDurability());
        } else {
            Slot1 = null;
        }
        if (event.getContents().getItem(2) != null) {
            Slot2 = new ItemStack(event.getContents().getItem(2).getType(), 1, (short) event.getContents().getItem(2).getDurability());
        } else {
            Slot2 = null;
        }


        //Check the brewed potions for banned potions. Delayed task to check the potions after brewing.
        Bukkit.getScheduler().scheduleSyncDelayedTask(dirtRestrict, () -> {
            List<Player> players = event.getContents().getViewers().stream()
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());
            Optional<Restriction> bannedInfo = Optional.empty();
            ItemKey itemKey = null;
            //Get all 3 brewing stand potion lots content
            ItemStack item0 = event.getContents().getItem(0);
            ItemStack item1 = event.getContents().getItem(1);
            ItemStack item2 = event.getContents().getItem(2);

            //Check slot 0 for banned items
            if (item0 != null) {
                itemKey = new ItemKey(item0.getData());
                bannedInfo = isRestricted(itemKey, RestrictionTypes.BREWING);
                if (bannedInfo.isPresent()) {
                    event.getContents().setItem(0, Slot0);
                }
            }
            //Check slot 1 for banned items
            if (item1 != null) {
                itemKey = new ItemKey(item1.getData());
                bannedInfo = isRestricted(itemKey, RestrictionTypes.BREWING);
                if (bannedInfo.isPresent()) {
                    event.getContents().setItem(1, Slot1);
                }
            }
            //Check slot 2 for banned items
            if (item2 != null) {
                itemKey = new ItemKey(item2.getData());
                bannedInfo = isRestricted(itemKey, RestrictionTypes.BREWING);
                if (bannedInfo.isPresent()) {
                    event.getContents().setItem(2, Slot2);
                }
            }
            //If there is a viewer on the brewing stand send the restricted message
            if (bannedInfo.isPresent() && !players.isEmpty()) {
                players.get(0).getInventory().addItem(new ItemStack(ingredient.getType(), 1));
                final ItemKey finalItemKey = itemKey;
                final Optional<Restriction> finalBannedInfo = bannedInfo;
                players.forEach(player->{
                    soundHandler.sendPlingSound(player);
                    printMessage(player, RestrictionTypes.BREWING, finalItemKey, finalBannedInfo.map(Restriction::getReason).orElse(null));
                });
            }
        }, 1L);
    }

}

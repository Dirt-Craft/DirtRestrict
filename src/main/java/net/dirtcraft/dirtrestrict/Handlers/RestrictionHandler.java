package net.dirtcraft.dirtrestrict.Handlers;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class RestrictionHandler implements Listener {
    protected final DirtRestrict dirtRestrict = DirtRestrict.getInstance();
    protected final RestrictionList restricts = dirtRestrict.getRestrictions();
    protected final SoundHandler soundHandler = dirtRestrict.getSoundHandler();

    protected void printMessage(Player p, RestrictionTypes type, ItemKey itemKey, String reason) {
        p.sendMessage(getMessage(type, itemKey, reason));
    }

    private String getMessage(RestrictionTypes type, ItemKey itemKey, String reason){
        switch (type){
            case OWN: return "You are not allowed to own this item!";
            case USE: return "You are not allowed to use this item!";
            case DROP: return "You are not allowed to drop this item!";
            case BREAK: return "You are not allowed to break this item!";
            case PLACE: return "You are not allowed to place this item!";
            case PICKUP: return "You are not allowed to pick up this item!";
            case BREWING: return "You are not allowed to brew this potion!";
            case CRAFTING: return "You are not allowed to craft this item!";
            case SMELTING: return  "You are not allowed to smelt this item!";
            case CREATIVE: return "This item cannot be used in creative!";
            default: return "This is not allowed.";
        }
    }
}

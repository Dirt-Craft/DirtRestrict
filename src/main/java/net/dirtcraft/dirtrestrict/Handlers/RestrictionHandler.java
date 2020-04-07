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
        String reasonText = reason == null || reason.equalsIgnoreCase("")? "§c!" : ("§c due to \"§4" + reason + "§c\"");
        p.sendMessage(getMessage(type, itemKey) + reasonText);
    }

    private String getMessage(RestrictionTypes type, ItemKey itemKey){
        switch (type){
            case OWN: return "§cYou are not allowed to own §6" + itemKey.getName();
            case USE: return "§cYou are not allowed to use §6" + itemKey.getName();
            case DROP: return "§cYou are not allowed to drop §6" + itemKey.getName();
            case BREAK: return "§cYou are not allowed to break §6" + itemKey.getName();
            case PLACE: return "§cYou are not allowed to place §6" + itemKey.getName();
            case PICKUP: return "§cYou are not allowed to pick up §6" + itemKey.getName();
            case BREWING: return "§cYou are not allowed to brew §6" + itemKey.getName();
            case CRAFTING: return "§cYou are not allowed to craft §6" + itemKey.getName();
            case SMELTING: return  "§cYou are not allowed to smelt §6" + itemKey.getName();
            case CREATIVE: return "§6" + itemKey.getName() + "§c cannot be used in creative";
            default: return "§6" + itemKey.getName() + "§c is not allowed";
        }
    }
}

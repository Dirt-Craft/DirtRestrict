package net.dirtcraft.dirtrestrict.Handlers;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Handlers.Restrictions.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public abstract class RestrictionHandler implements Listener {
    protected final DirtRestrict dirtRestrict = DirtRestrict.getInstance();
    protected final RestrictionList restricts = dirtRestrict.getRestrictions();
    protected final SoundHandler soundHandler = dirtRestrict.getSoundHandler();

    protected Optional<Restriction> isRestricted(ItemKey key, RestrictionTypes type){
        Optional<Restriction> optRestriction = restricts.getRestriction(key);
        if (!optRestriction.isPresent()) optRestriction = restricts.getRestriction(key.getAll());
        if (optRestriction.isPresent() && optRestriction.get().isRestricted(type)) return optRestriction;
        else return Optional.empty();
    }

    protected void printMessage(Player p, RestrictionTypes type, ItemKey itemKey, String reason) {
        p.sendMessage(reason);
    }

    protected boolean hasPermission(Player player, ItemKey itemKey, RestrictionTypes type){
        return checkPerms(player, itemKey.getUniqueIdentifier(), String.valueOf(itemKey.data), type.toString().toLowerCase());
    }

    private boolean checkPerms(Player player, String itemId, String meta, String type){
        if (checkPerm(player, "*", meta, type)) return true;
        if (checkPerm(player, itemId, "*", type)) return true;
        if (checkPerm(player, itemId, meta, type)) return true;
        return false;
    }

    private boolean checkPerm(Player player, String... check){
        StringBuilder sb = new StringBuilder("dirtrestrict.bypass.");
        Arrays.stream(check).forEach(sb::append);
        return player.hasPermission(sb.toString());
    }
}

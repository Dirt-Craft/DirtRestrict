package net.dirtcraft.dirtrestrict.Handlers;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHandler extends RestrictionHandler {
    private final boolean sounds = true;
    public void sendAnvilLandSound(Player p) {
        if (sounds) p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 1);
        //if (sounds) p.playSound(p.getLocation(), Sound.valueOf("ANVIL_LAND"), 1, 1);
    }

    public void sendEndermanTeleportSound(Player p) {
        if (sounds) p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
        //if (sounds) p.playSound(p.getLocation(), Sound.valueOf("ENDERMAN_TELEPORT"), 1, 1);
    }

    public void sendItemBreakSound(Player p) {
        if (sounds) p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1, 1);
        //if (sounds) p.playSound(p.getLocation(), Sound.valueOf("ITEM_BREAK"), 1, 1);
    }

    public void sendPlingSound(Player p) {
        if (sounds) p.playSound(p.getLocation(), Sound.NOTE_PLING, 3F, 3F);
        //if (sounds) p.playSound(p.getLocation(), Sound.valueOf("NOTE_PLING"), 3F, 3F);
    }

    public void sendLevelUpSound(Player p) {
        if (sounds) p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
        //if (sounds) p.playSound(p.getLocation(), Sound.valueOf("LEVEL_UP"), 1F, 1F);
    }

    public void sendArrowHit(Player p) {
        if (sounds) p.playSound(p.getLocation(), Sound.ARROW_HIT, 1F, 1F);
        //if (sounds) p.playSound(p.getLocation(), Sound.valueOf("SUCCESSFUL_HIT"), 1F, 1F);
    }
}

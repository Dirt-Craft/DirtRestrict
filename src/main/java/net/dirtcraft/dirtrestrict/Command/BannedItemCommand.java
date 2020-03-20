package net.dirtcraft.dirtrestrict.Command;

import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BannedItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        ItemStack is = ((Player)sender).getItemInHand();
        DirtRestrict.getInstance().getRestrictions().addBan(is);
        DirtRestrict.getInstance().getRestrictions().save();
        return false;
    }
}

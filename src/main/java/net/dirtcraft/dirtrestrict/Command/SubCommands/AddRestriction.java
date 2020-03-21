package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddRestriction implements SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_ADD;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && sender instanceof Player){
            DirtRestrict.getInstance().getRestrictions().addBan(((Player) sender).getItemInHand());
            return true;
        }
        return false;
    }
}

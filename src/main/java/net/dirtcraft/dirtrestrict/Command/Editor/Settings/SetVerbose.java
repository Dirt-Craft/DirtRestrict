package net.dirtcraft.dirtrestrict.Command.Editor.Settings;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetVerbose implements SubCommand {
    public static final String ALIAS = "SetVerbose";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.PERMISSION_ADMIN;
    }

    @Override
    public boolean onCommand(CommandSender p, Command command, String s, String[] args) {
        if (!(p instanceof Player) || args.length < 1 || args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false")) return false;
        boolean set = args[0].equalsIgnoreCase("true");
        DirtRestrict.getInstance().getPreferences().setVerbose((Player) p, set);
        return true;
    }
}

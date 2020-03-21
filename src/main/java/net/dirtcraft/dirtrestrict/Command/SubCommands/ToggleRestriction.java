package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionType;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ToggleRestriction implements SubCommand {
    @Override
    public String getName() {
        return "toggle";
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_TYPE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final RestrictionList restrictions = DirtRestrict.getInstance().getRestrictions();
        if (args.length < 3) return false;
        Material material = Material.getMaterial(Integer.parseInt(args[0]));
        if (material == null) material = Material.getMaterial(args[0]);
        if (material == null) return false;

        Byte b = Byte.parseByte(args[1]);
        if (args[1].equalsIgnoreCase("-a")) b = null;

        RestrictionType type = RestrictionType.valueOf(args[2]);
        if (type == null) return false;

        restrictions.updateBanType(new ItemKey(material, b), type);

        return true;
    }
}

package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

public class SetReason implements SubCommand {
    @Override
    public String getName() {
        return "reason";
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_REASON;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final RestrictionList restrictions = DirtRestrict.getInstance().getRestrictions();
        final int i;
        if (args.length < 1) return false;
        final Optional<Material> material = parseMaterial(args[0]);
        final Optional<Byte> b = parseByte(args[1]);
        if (material.isPresent() && args.length > 2 && (b.isPresent() || args[1].equalsIgnoreCase("-"))) i = 2;
        else if (material.isPresent()) i = 1;
        else return false;

        final String[] remaining = new String[args.length - i];
        System.arraycopy(args, i, remaining, 0, args.length - i );
        final String reason = String.join(" ", remaining);

        restrictions.updateBanReason(new ItemKey(material.get(), b.orElse(null)), reason);

        return true;
    }
}

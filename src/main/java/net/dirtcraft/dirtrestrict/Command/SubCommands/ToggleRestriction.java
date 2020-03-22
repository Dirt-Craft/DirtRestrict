package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Optional;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

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
        if (args.length < 2) return false;
        final Optional<Material> material = parseMaterial(args[0]);
        final Optional<Byte> b;
        final Optional<RestrictionTypes> type;
        if (args.length > 2) {
            b = parseByte(args[1]);
            type = parseType(args[2]);
        } else {
            b = Optional.empty();
            type = parseType(args[1]);
        }

        if ( !material.isPresent() || !type.isPresent() ) return false;

        restrictions.updateBanType(new ItemKey(material.get(), b.orElse(null)), type.get());

        return true;
    }
}

package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

public class RemoveRestriction implements SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_REMOVE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final RestrictionList restrictions = DirtRestrict.getInstance().getRestrictions();
        if (args.length < 1) return false;
        final Optional<Material> material = parseMaterial(args[0]);
        final Optional<Byte> b;
        if (!material.isPresent()) {
            return false;
        } else if (args.length > 1) {
            b = parseByte(args[1]);
        } else {
            b = Optional.empty();
        }
        final ItemKey bannedItem = new ItemKey(material.get(), b.orElse(null));
        final boolean success = restrictions.revokeBan(bannedItem);
        final String response = "§aUnbanned §r\"§5" + bannedItem.getName() + "§r\"";

        if (success) sender.sendMessage(response);
        else sender.sendMessage("\"§5" + bannedItem.getName() + "§r\"§c does not exist on the ban list.");
        if (sender instanceof Player && success) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(bannedItem));

        return success;
    }
}

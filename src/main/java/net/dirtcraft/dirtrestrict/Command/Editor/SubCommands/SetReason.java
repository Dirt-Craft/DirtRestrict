package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands;

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

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseByte;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseMaterial;

public class SetReason implements SubCommand {

    public static final String ALIAS = "Reason";
    @Override
    public String getName() {
        return ALIAS;
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
        final ItemKey bannedItem = new ItemKey(material.get(), b.orElse(null));
        final boolean success = restrictions.updateBanReason(bannedItem, reason);
        final String response = "§aBan reason of §r\"§5" + bannedItem.getName() + "§r\"§a set to §r\"§5" + reason + "§r\"";

        if (success) sender.sendMessage(response);
        else sender.sendMessage("\"§5" + bannedItem.getName() + "§r\"§c does not exist on the ban list.");
        if (sender instanceof Player && success) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(bannedItem));

        return success;
    }
}

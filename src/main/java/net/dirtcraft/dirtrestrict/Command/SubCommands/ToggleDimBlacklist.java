package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseByte;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseMaterial;

public class ToggleDimBlacklist implements SubCommand {
    public final static String ALIAS = "DimBlacklist";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_DIMS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
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
        final Optional<Restriction> restriction = restrictions.getRestriction(bannedItem);
        AtomicBoolean x = new AtomicBoolean(false);
        restriction.ifPresent(res-> x.set(res.toggleBlacklist()));
        final String response = "§aBlacklist set to " + x.get() + " for §r\"§5" + bannedItem.getName() + "§r\"";

        if (restriction.isPresent()) sender.sendMessage(response);
        else sender.sendMessage("\"§5" + bannedItem.getName() + "§r\"§c does not exist on the ban list.");
        if (sender instanceof Player && restriction.isPresent()) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(bannedItem, false, true));

        return restriction.isPresent();
    }
}

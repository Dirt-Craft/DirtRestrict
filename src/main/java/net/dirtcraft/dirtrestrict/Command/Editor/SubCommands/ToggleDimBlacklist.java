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
        Optional<Boolean> blacklist = restrictions.toggleBlacklist(bannedItem);
        if (!blacklist.isPresent()) return false;
        final String response = "§aDims set to " + (!blacklist.get()? "blacklist" : "whitelist") + " for §r\"§5" + bannedItem.getName() + "§r\"";

        sender.sendMessage(response);
        if (sender instanceof Player) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(bannedItem));

        return true;
    }
}

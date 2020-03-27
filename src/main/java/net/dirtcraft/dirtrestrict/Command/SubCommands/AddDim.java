package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

public class AddDim implements SubCommand {
    public static final String ALIAS = "AddDim";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_DIMS;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final RestrictionList restrictions = DirtRestrict.getInstance().getRestrictions();
        if (args.length < 1) return false;

        Optional<Material> material = parseMaterial(args[0]);
        Optional<Byte> meta = args.length > 1 ? parseByte(args[1]) : Optional.empty();
        if (!material.isPresent()) return false;

        ItemKey itemKey = new ItemKey(material.get(), meta.orElse(null));
        final Optional<Boolean> blacklist = restrictions.isBlackList(itemKey);
        final Optional<Boolean> response;
        if (args.length > 2) response = parseWorld(args[2]).flatMap(w->restrictions.addDim(itemKey, w));
        else if (args.length == 2 && !meta.isPresent()) response = parseWorld(args[1]).flatMap(w->restrictions.addDim(itemKey, w));
        else if (sender instanceof Player) response = Optional.ofNullable(((Player)sender).getWorld())
                .flatMap(w->restrictions.addDim(itemKey, w));
        else return false;
        if (!response.isPresent() || !blacklist.isPresent()) return false;

        if (!response.get()){
            sender.sendMessage("§cThe specified restriction does not exist.");
        } else {
            sender.sendMessage("§aSuccessfully added world to " + (blacklist.get()? "blacklist." : "whitelist."));
            if (sender instanceof Player) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(itemKey));
        }


        return true;
    }
}

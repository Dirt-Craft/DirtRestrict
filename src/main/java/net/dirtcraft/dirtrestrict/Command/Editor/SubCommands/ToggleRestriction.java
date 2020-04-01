package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

public class ToggleRestriction implements SubCommand {

    public static final String ALIAS = "Toggle";
    @Override
    public String getName() {
        return ALIAS;
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
        final ItemKey bannedItem = new ItemKey(material.get(), b.orElse(null));
        final Optional<Boolean> success = restrictions.toggleBanType(bannedItem, type.get());

        if (!success.isPresent()) {
            sender.sendMessage("\"§5" + bannedItem.getName() + "§r\"§c does not exist on the ban list.");
            return false;
        }
        final String status = success.get() ? "§4Restricted" : "§2Allowed";
        final String response = "§aToggled §6" + type.get().getName() + "§a to " + status + "§a for §r\"§5" + bannedItem.getName() + "§r\"";

        sender.sendMessage(response);
        if (sender instanceof Player) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(bannedItem));

        return true;
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            return DirtRestrict.getInstance().getRestrictions().getRestrictions().keySet()
                    .stream()
                    .map(ItemKey::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else if (strings.length == 1){
            Optional<Material> optMaterial = parseMaterial(strings[0]);
            if (!optMaterial.isPresent()) return new ArrayList<>();
            int item = optMaterial.get().getId();
            return DirtRestrict.getInstance().getRestrictions().getRestrictions().keySet()
                    .stream()
                    .filter(key->key.item == item)
                    .map(ItemKey::getMeta)
                    .collect(Collectors.toList());
        } else if (strings.length == 2){
            return Arrays.stream(RestrictionTypes.values())
                    .map(Enum::toString)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

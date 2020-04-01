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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseByte;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseMaterial;

public class ToggleHidden implements SubCommand {
    public final static String ALIAS = "ToggleHidden";
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
        Optional<Boolean> hidden = restrictions.toggleHidden(bannedItem);
        if (!hidden.isPresent()) return false;
        final String response = (hidden.get()? "§cHidden" : "§aRevealed") + " the entry for §r\"§5" + bannedItem.getName() + "§r\"'.";

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
        }
        return new ArrayList<>();
    }
}

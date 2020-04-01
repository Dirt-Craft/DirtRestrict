package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

public class RemoveDim implements SubCommand {
    public static final String ALIAS = "RemoveDim";
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
        if (args.length > 2) response = parseWorld(args[2]).flatMap(w->restrictions.removeDim(itemKey, w));
        else if (args.length == 2 && !meta.isPresent()) response = parseWorld(args[1]).flatMap(w->restrictions.removeDim(itemKey, w));
        else if (sender instanceof Player) response = Optional.ofNullable(((Player)sender).getWorld())
                .flatMap(w->restrictions.addDim(itemKey, w));
        else return false;
        if (!response.isPresent() || !blacklist.isPresent()) return false;

        if (!response.get()){
            sender.sendMessage("§cThe specified restriction does not exist.");
        } else {
            sender.sendMessage("§aSuccessfully removed world from " + (!blacklist.get()? "blacklist." : "whitelist."));
            if (sender instanceof Player) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(itemKey));
        }


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
        Optional<Material> optMaterial = parseMaterial(strings[0]);
        Optional<Byte> optMeta = parseByte(strings[1]);
        if (!optMaterial.isPresent() || !optMeta.isPresent() && !strings[1].equalsIgnoreCase("*") && !strings[1].equalsIgnoreCase("-u")){
            return new ArrayList<>();
        }
        ItemKey itemKey = new ItemKey(optMaterial.get().getId(), optMeta.orElse(null));
        Optional<Restriction> optionalRestriction = DirtRestrict.getInstance().getRestrictions().getRestriction(itemKey);
        return optionalRestriction
                .map(restriction -> restriction.getDims()
                        .stream()
                        .map(Bukkit.getServer()::getWorld)
                        .map(World::getName)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}

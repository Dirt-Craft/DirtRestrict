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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseByte;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseMaterial;

public class AddRestriction implements SubCommand {

    public static final String ALIAS = "Add";

    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_ADD;
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) return new ArrayList<>();
        return Arrays.stream(Material.values()).map(Material::getId).map(String::valueOf).collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RestrictionList restrictions = DirtRestrict.getInstance().getRestrictions();
        Optional<Material> material;
        Optional<Byte> b;
        Optional<String> reason = Optional.empty();
        if (args.length == 0 && sender instanceof Player){
            final ItemStack hand = (((Player) sender).getItemInHand());
            material = Optional.of(hand.getType());
            b = Optional.of(hand.getData().getData());
        } else if (args.length == 1 && args[0].equalsIgnoreCase("-u")){
            final ItemStack hand = (((Player) sender).getItemInHand());
            material = Optional.of(hand.getType());
            b = Optional.empty();
        } else if(args.length == 1) {
            material = parseMaterial(args[0]);
            b = Optional.empty();
            if (!material.isPresent()){
                final ItemStack hand = (((Player) sender).getItemInHand());
                material = Optional.of(hand.getType());
                b = Optional.of(hand.getData().getData());
                reason = Optional.of(args[0]);
            }
        } else {
            material = parseMaterial(args[0]);
            b = parseByte(args[1]);
            int i = 0;
            if (material.isPresent()) {
                i++;
                if (b.isPresent() || args[1].equalsIgnoreCase("-u")) i++;
            } else if(args[0].equalsIgnoreCase("-u")) {
                final ItemStack hand = (((Player) sender).getItemInHand());
                material = Optional.of(hand.getType());
                b = Optional.empty();
                i++;
            } else {
                final ItemStack hand = (((Player) sender).getItemInHand());
                material = Optional.of(hand.getType());
                b = Optional.of(hand.getData().getData());
            }
            if (i < args.length) {
                String[] arr = new String[args.length-i];
                System.arraycopy(args, i, arr, 0, arr.length);
                reason = Optional.of(String.join(" ", arr));
            }
        }

        final ItemKey bannedItem = new ItemKey(material.get(), b.orElse(null));
        if (bannedItem.getItem() == null) return false;
        final boolean success = restrictions.addBan(bannedItem, reason.orElse(null));
        final String response = "§aAdded §r\"§5" + bannedItem.getName() +"§r\" §ato the ban list.";

        if (success) sender.sendMessage(response);
        else sender.sendMessage("§cThis item is already banned!");
        if (sender instanceof Player && success) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(bannedItem));

        return success;
    }
}

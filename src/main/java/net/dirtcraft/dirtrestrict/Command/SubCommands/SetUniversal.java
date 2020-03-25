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
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseByte;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseMaterial;

public class SetUniversal implements SubCommand {

    public static final String ALIAS = "SetUniversal";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_META;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        RestrictionList restrictions = DirtRestrict.getInstance().getRestrictions();
        final Optional<Material> material;
        final Optional<Byte> b;
        if (args.length == 0 && sender instanceof Player){
            final ItemStack hand = (((Player) sender).getItemInHand());
            material = Optional.of(hand.getType());
            b = Optional.of(hand.getData().getData());
        } else if(args.length == 1) {
            material = parseMaterial(args[0]);
            b = Optional.empty();
        } else {
            material = parseMaterial(args[0]);
            b = parseByte(args[1]);
        }

        if ( !material.isPresent() ) return false;
        final ItemKey bannedItem = new ItemKey(material.get(), b.orElse(null));
        final boolean success = restrictions.removeMeta(bannedItem);
        final String response = "§aChanged §r\"§5" + bannedItem.getName() +"§r\" §ato ignore meta.";

        if (success) sender.sendMessage(response);
        else sender.sendMessage("§cThis item does not exist!");
        if (sender instanceof Player && success) ((Player)sender).spigot().sendMessage(TextUtils.getLinks(bannedItem));

        return success;
    }
}

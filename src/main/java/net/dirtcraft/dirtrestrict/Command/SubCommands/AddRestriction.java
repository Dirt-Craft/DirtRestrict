package net.dirtcraft.dirtrestrict.Command.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.Configuration.RestrictionList;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseByte;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseMaterial;

public class AddRestriction implements SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_ADD;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        else {
            restrictions.addBan(new ItemKey(material.get(), b.orElse(null)));
        }

        return false;
    }
}

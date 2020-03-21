package net.dirtcraft.dirtrestrict.Command;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.Flipper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.material.MaterialData;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

import java.util.Map;

public class BannedItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Map<ItemKey, Restriction> restrictionMap = DirtRestrict.getInstance().getRestrictions().getRestrictions();
        final int page = args.length > 0 ? parsePage(args[0], restrictionMap.size()) : 1;
        final Flipper<Character> mainColor = new Flipper<>('6', 'e');
        final Flipper<Character> sideColor = new Flipper<>('3', 'b');
        final Flipper<Character> metaColor = new Flipper<>('d', '5');

        restrictionMap.forEach(((itemKey, restriction) -> {
            if (itemKey.data == null) return;
            final String side = "§" + sideColor.get();
            final String hyphen = side + " - §r";
            final String itemName = "§" + mainColor.get() + new MaterialData(itemKey.material, itemKey.data).toItemStack(1).getData().toString();
            final String id = " §r(§" + metaColor.get() + itemKey.material.getId() + ":" + itemKey.data + "§r)";
            final String reason = hyphen + " " + side + restriction.getReason();
            sender.sendMessage(hyphen + itemName + id + (restriction.getReason().equalsIgnoreCase("") ? "" : reason));
        }));

        return true;
    }
}

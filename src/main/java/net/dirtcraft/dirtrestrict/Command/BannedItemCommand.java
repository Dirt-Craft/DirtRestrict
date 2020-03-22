package net.dirtcraft.dirtrestrict.Command;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.Flipper;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

import java.util.Map;

public class BannedItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Map<ItemKey, Restriction> restrictionMap = DirtRestrict.getInstance().getRestrictions().getRestrictions();
        final int page = args.length > 0 ? parsePage(args[0], restrictionMap.size()) : 1;
        final Flipper<Character> mainColor = new Flipper<>('6', 'e');
        final Flipper<Character> trimColor = new Flipper<>('3', 'b');
        final Flipper<Character> lastColor = new Flipper<>('8', '7');

        sender.sendMessage("§4§m====[§r §cDIRT§fCRAFT §4§m]=[§r §5BANNED ITEMS §4§m]====");
        restrictionMap.forEach(((itemKey, restriction) -> {
            if (itemKey.data == null) return;
            final String side = "§" + trimColor.get();
            final String hyphen = side + " - §r";
            final String itemName = "§" + mainColor.get() + itemKey.getName();
            final String id = " §r[§b" + itemKey.item + "§r:§3" + itemKey.data + "§r]";
            final String reason = hyphen + "§" + lastColor.get() + restriction.getReason();

            sender.sendMessage(hyphen + itemName + id + (restriction.getReason().equalsIgnoreCase("") ? "" : reason));
        }));
        sender.sendMessage("§4§m=============[§4 « 1/1 » §4§m]=============");

        return true;
    }
}

package net.dirtcraft.dirtrestrict.Command;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.Flipper;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;


import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BannedItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        final Player player = (Player) sender;
        final Map<ItemKey, Restriction> restrictionMap = DirtRestrict.getInstance().getRestrictions().getRestrictions();
        //final int page = args.length > 0 ? parsePage(args[0], restrictionMap.size()) : 1;
        final Flipper<Character> mainColor = new Flipper<>('6', 'e');
        final Flipper<Character> trimColor = new Flipper<>('3', 'b');
        final Flipper<Character> lastColor = new Flipper<>('8', '7');
        final List<BaseComponent> entries = new ArrayList<>();

        sender.sendMessage("§4§m====[§r §cDIRT§fCRAFT §4§m]=[§r §5BANNED ITEMS §4§m]====");
        restrictionMap.forEach(((itemKey, restriction) -> entries.add(TextUtils.getRestrictionText(itemKey, restriction, mainColor.get(), trimColor.get(), lastColor.get(), true))));
        entries.forEach(player.spigot()::sendMessage);
        sender.sendMessage("§4§m=============[§4 « 1/1 » §4§m]=============");

        return true;
    }
}

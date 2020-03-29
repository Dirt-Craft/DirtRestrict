package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseByte;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.parseMaterial;

public class EditRestriction implements SubCommand {

    public static final String ALIAS = "Edit";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_BASE;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1 || !(sender instanceof Player)) return false;
        final Player player = (Player) sender;
        final Optional<Material> optItem = parseMaterial(args[0]);
        final Optional<Byte> optByte = args.length > 1? parseByte(args[1]) : Optional.empty();
        if (!optItem.isPresent()) return false;

        final ItemKey itemKey = new ItemKey(optItem.get(), optByte.orElse(null));
        final Optional<Restriction> restriction = DirtRestrict.getInstance().getRestrictions().getRestriction(itemKey);

        if (!restriction.isPresent()) {
            sender.sendMessage("§4Error: §cThis item is not restricted!");
            return false;
        }

        sender.sendMessage("§4§m=============[§4§l EDITOR §4§m]=============");
        player.spigot().sendMessage(TextUtils.getRemoveLinks(itemKey, restriction.get(), player.getWorld()));
        sender.sendMessage("§6Name: §7" + itemKey.getName() + " §r[§b" + itemKey.item + (itemKey.data == null? "§r]" : "§r:§3" + itemKey.data + "§r]"));
        sender.sendMessage("§6ID: §7" + itemKey.getUniqueIdentifier());
        player.spigot().sendMessage(TextUtils.getWorlds(restriction.get().getDims(), itemKey, restriction.get().isDimsBlacklist()));
        player.spigot().sendMessage(TextUtils.getReason(itemKey, restriction.get()));
        TextUtils.getToggleLinks(itemKey, restriction.get()).forEach(player.spigot()::sendMessage);
        sender.sendMessage("§4§m==================================");
        return false;
    }
}

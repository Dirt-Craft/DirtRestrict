package net.dirtcraft.dirtrestrict.Command.SubCommands;

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

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

import java.util.Optional;

public class EditRestriction implements SubCommand {
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_BASE;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2 || !(commandSender instanceof Player)) return false;
        final Player player = (Player) commandSender;
        final Optional<Material> optItem = parseMaterial(strings[0]);
        final Optional<Byte> optByte = parseByte(strings[1]);
        if (!optItem.isPresent()) return false;

        final ItemKey itemKey = new ItemKey(optItem.get(), optByte.orElse(null));
        final Optional<Restriction> restriction = DirtRestrict.getInstance().getRestrictions().getRestriction(itemKey);

        if (!restriction.isPresent()) {
            commandSender.sendMessage("§4Error: §cThis item is not restricted!");
            return false;
        }

        commandSender.sendMessage("§4§m=============[§4§l EDITOR §4§m]=============");
        player.spigot().sendMessage(TextUtils.getRemoveLink(itemKey));
        commandSender.sendMessage("§6Name: §7" + itemKey.getName() + " §r[§b" + itemKey.item + (itemKey.data == null? "§r]" : "§r:§3" + itemKey.data + "§r]"));
        commandSender.sendMessage("§6ID: §7" + itemKey.getUniqueIdentifier());
        player.spigot().sendMessage(TextUtils.getReason(itemKey, restriction.get()));
        TextUtils.getToggleLinks(itemKey, restriction.get()).forEach(player.spigot()::sendMessage);
        commandSender.sendMessage("§4§m==================================");
        return false;
    }
}

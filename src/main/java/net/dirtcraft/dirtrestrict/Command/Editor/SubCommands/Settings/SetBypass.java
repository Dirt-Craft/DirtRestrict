package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.BypassSettings;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetBypass implements SubCommand {
    public static final String ALIAS = "SetBypass";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.PERMISSION_ADMIN;
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length > 0) return null;
        return Arrays.stream(BypassSettings.values()).map(Enum::toString).collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player) || strings.length < 1) return false;
        Optional<BypassSettings> setting = CommandUtils.parseEnum(BypassSettings.class, strings[0]);
        if (!setting.isPresent()) return false;
        else DirtRestrict.getInstance().getPreferences().setBypassSettings((Player)commandSender, setting.get());
        return true;
    }
}

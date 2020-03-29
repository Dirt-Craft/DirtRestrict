package net.dirtcraft.dirtrestrict.Command.Editor.Settings;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.BypassSettings;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.CommandUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SetSound implements SubCommand {
    public static final String ALIAS = "SetSound";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.PERMISSION_ADMIN;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player) || strings.length < 1) return false;
        Optional<Sound> setting = CommandUtils.parseEnum(Sound.class, strings[0]);
        if (!setting.isPresent()) return false;
        else DirtRestrict.getInstance().getPreferences().setSound((Player)commandSender, setting.get());
        return true;
    }
}

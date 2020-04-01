package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands;

import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings.SetBypass;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings.SetSound;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings.SetVerbose;
import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.AdminProfile;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsBase implements SubCommand{
    public static final String ALIAS = "Settings";
    private final Map<String, SubCommand> subCommandMap = new HashMap<>();

    {
        addCommand(new SetBypass());
        addCommand(new SetVerbose());
        addCommand(new SetSound());
    }

    private void addCommand(SubCommand command){
        subCommandMap.put(command.getName().toLowerCase(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (args.length == 0) return onCommandDefault((Player) sender);

        final String sub = args[0].toLowerCase();
        SubCommand subCommand = subCommandMap.get(sub);
        if (subCommand == null) return false;

        final String[] newArgs = new String[args.length-1];
        System.arraycopy(args, 1, newArgs, 0, args.length-1);
        if (sender.hasPermission(subCommand.getPermission())) subCommand.onCommand(sender, command, sub, newArgs);
        else sender.sendMessage("§cYou do not have permission to use that command.");
        return true;
    }

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
        if (strings.length == 0) {
            return new ArrayList<>(subCommandMap.keySet());
        } else if (subCommandMap.containsKey(strings[0].toLowerCase())) {
            String[] args = new String[strings.length-1];
            System.arraycopy(strings, 1, args, 0, args.length);
            return subCommandMap.get(strings[1].toLowerCase()).getTabComplete(commandSender, command, s, args);
        } else {
            return new ArrayList<>();
        }
    }

    public boolean onCommandDefault(Player sender){
        AdminProfile profile = DirtRestrict.getInstance().getPreferences().getPreferences(sender);
        sender.sendMessage("§4§m=========[§r §cSETTINGS §4§m]=========");
        sender.spigot().sendMessage(TextUtils.getBypassSlider(profile));
        sender.spigot().sendMessage(TextUtils.getVerboseSlider(profile));
        sender.sendMessage("§4§m============================");
        return true;
    }
}
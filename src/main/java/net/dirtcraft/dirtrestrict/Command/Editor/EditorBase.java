package net.dirtcraft.dirtrestrict.Command.Editor;

import net.dirtcraft.dirtrestrict.Command.Editor.Settings.SettingsBase;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.*;
import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class EditorBase implements CommandExecutor {
    private final Map<String, SubCommand> subCommandMap = new HashMap<>();

    {
        addCommand(new ToggleRestriction());
        addCommand(new RemoveRestriction());
        addCommand(new SetReason());
        addCommand(new AddRestriction());
        addCommand(new EditRestriction());
        addCommand(new SetUniversal());
        addCommand(new ToggleDimBlacklist());
        addCommand(new AddDim());
        addCommand(new RemoveDim());
        addCommand(new SettingsBase());
    }

    private void addCommand(SubCommand command){
        subCommandMap.put(command.getName().toLowerCase(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.COMMAND_MODIFY_BASE) || args.length == 0) return false;

        final String sub = args[0].toLowerCase();
        SubCommand subCommand = subCommandMap.get(sub);
        if (subCommand == null) return false;

        final String[] newArgs = new String[args.length-1];
        System.arraycopy(args, 1, newArgs, 0, args.length-1);
        if (sender.hasPermission(subCommand.getPermission())) subCommand.onCommand(sender, command, sub, newArgs);
        else sender.sendMessage("§cYou do not have permission to use that command.");
        return true;
    }
}

package net.dirtcraft.dirtrestrict.Command;

import net.dirtcraft.dirtrestrict.Command.SubCommands.AddRestriction;
import net.dirtcraft.dirtrestrict.Command.SubCommands.EditReason;
import net.dirtcraft.dirtrestrict.Command.SubCommands.RemoveRestriction;
import net.dirtcraft.dirtrestrict.Command.SubCommands.ToggleRestriction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class DirtRestrictCommand implements CommandExecutor {
    private final Map<String, SubCommand> subCommandMap = new HashMap<>();

    {
        addCommand(new ToggleRestriction());
        addCommand(new RemoveRestriction());
        addCommand(new EditReason());
        addCommand(new AddRestriction());
    }

    private void addCommand(SubCommand command){
        subCommandMap.put(command.getName(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        final String sub = args[0];
        SubCommand subCommand = subCommandMap.get(sub);
        if (subCommand == null) return false;

        final String[] newArgs = new String[args.length-1];
        System.arraycopy(args, 1, newArgs, 0, args.length-1);
        if (sender.hasPermission(subCommand.getPermission())) subCommand.onCommand(sender, command, sub, newArgs);
        else sender.sendMessage("§cYou do not have permission to use that command.");
        return true;
    }
}

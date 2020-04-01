package net.dirtcraft.dirtrestrict.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand extends CommandExecutor {
    String getName();
    String getPermission();
    List<String> getTabComplete(CommandSender commandSender, Command command, String s, String[] strings);
}

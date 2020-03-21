package net.dirtcraft.dirtrestrict.Command;

import org.bukkit.command.CommandExecutor;

public interface SubCommand extends CommandExecutor {
    String getName();
    String getPermission();
}

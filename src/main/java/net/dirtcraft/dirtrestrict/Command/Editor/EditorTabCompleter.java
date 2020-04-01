package net.dirtcraft.dirtrestrict.Command.Editor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class EditorTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 2) {
            return new ArrayList<>(EditorBase.subCommandMap.keySet());
        } else if (EditorBase.subCommandMap.containsKey(strings[0].toLowerCase())) {
            String[] args = new String[strings.length-2];
            System.arraycopy(strings, 1, args, 0, args.length);
            return EditorBase.subCommandMap.get(strings[0].toLowerCase()).getTabComplete(commandSender, command, s, args);
        } else {
            return new ArrayList<>();
        }
    }
}

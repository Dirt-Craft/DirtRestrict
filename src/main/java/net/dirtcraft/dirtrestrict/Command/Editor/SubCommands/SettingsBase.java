package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands;

import com.google.common.collect.Lists;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings.SetBypass;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings.SetHidden;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings.SetSound;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.Settings.SetVerbose;
import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.AdminProfile;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.BypassSettings;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static net.dirtcraft.dirtrestrict.Utility.TextUtils.*;

public class SettingsBase implements SubCommand{
    public static final String ALIAS = "Settings";
    private final Map<String, SubCommand> subCommandMap = new HashMap<>();

    {
        addCommand(new SetBypass());
        addCommand(new SetVerbose());
        addCommand(new SetSound());
        addCommand(new SetHidden());
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

    private boolean onCommandDefault(Player sender){
        AdminProfile profile = DirtRestrict.getInstance().getPreferences().getPreferences(sender);
        sender.sendMessage("§4§m=========[§r §cSETTINGS §4§m]=========");
        sender.spigot().sendMessage(getBypassSlider(profile));
        sender.spigot().sendMessage(getVerboseSlider(profile));
        sender.spigot().sendMessage(getHiddenSlider(profile));
        sender.sendMessage("§4§m============================");
        return true;
    }

    private static BaseComponent[] getBypassSlider(AdminProfile profile){
        final ArrayList<BaseComponent> result = new ArrayList<>();
        result.add(new TextComponent("§6Bypass: "));
        final boolean isRespect = profile.getBypassSetting() == BypassSettings.RESPECT;
        final boolean isNotify = profile.getBypassSetting() == BypassSettings.NOTIFY;
        final boolean isIgnore = profile.getBypassSetting() == BypassSettings.IGNORE;

        final BaseComponent[] respect = TextComponent.fromLegacyText(!isRespect? "§7RESPECT§r " : "§aRESPECT§r ");
        if (isRespect) result.addAll(Lists.newArrayList(respect));
        else {
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick toggle setting."));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SettingsBase.ALIAS, SetBypass.ALIAS, BypassSettings.RESPECT.toString()));
            Arrays.stream(respect)
                    .peek(t->t.setHoverEvent(hoverEvent))
                    .peek(t->t.setClickEvent(clickEvent))
                    .forEach(result::add);
        }

        final BaseComponent[] notify = TextComponent.fromLegacyText(!isNotify? "§7NOTIFY§r " : "§aNOTIFY§r ");
        if (isNotify) result.addAll(Lists.newArrayList(notify));
        else {
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick toggle setting."));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SettingsBase.ALIAS, SetBypass.ALIAS, BypassSettings.NOTIFY.toString()));
            Arrays.stream(notify)
                    .peek(t->t.setHoverEvent(hoverEvent))
                    .peek(t->t.setClickEvent(clickEvent))
                    .forEach(result::add);
        }

        final BaseComponent[] ignore = TextComponent.fromLegacyText(!isIgnore? "§7IGNORE§r " : "§aIGNORE§r ");
        if (isIgnore) result.addAll(Lists.newArrayList(ignore));
        else {
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick toggle setting."));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SettingsBase.ALIAS, SetBypass.ALIAS, BypassSettings.IGNORE.toString()));
            Arrays.stream(ignore)
                    .peek(t->t.setHoverEvent(hoverEvent))
                    .peek(t->t.setClickEvent(clickEvent))
                    .forEach(result::add);
        }
        return result.toArray(new BaseComponent[0]);
    }

    private static BaseComponent[] getVerboseSlider(AdminProfile profile){
        final ArrayList<BaseComponent> result = new ArrayList<>();
        result.add(new TextComponent("§6Verbose: "));
        final boolean verbose = profile.isShowPermissionNodes();
        final BaseComponent[] setYes = TextComponent.fromLegacyText(!verbose? "§7YES§r " : "§aYES§r ");
        final BaseComponent[] setNo = TextComponent.fromLegacyText(verbose? "§7NO§r " : "§aNO§r ");
        if (verbose) {
            result.addAll(Lists.newArrayList(setYes));
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick toggle setting."));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SettingsBase.ALIAS, SetVerbose.ALIAS, "false"));
            Arrays.stream(setNo)
                    .peek(t->t.setHoverEvent(hoverEvent))
                    .peek(t->t.setClickEvent(clickEvent))
                    .forEach(result::add);
        }
        else {
            result.addAll(Lists.newArrayList(setNo));
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick toggle setting."));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SettingsBase.ALIAS, SetVerbose.ALIAS, "true"));
            Arrays.stream(setYes)
                    .peek(t->t.setHoverEvent(hoverEvent))
                    .peek(t->t.setClickEvent(clickEvent))
                    .forEach(result::add);
        }
        return result.toArray(new BaseComponent[0]);
    }

    private static BaseComponent[] getHiddenSlider(AdminProfile profile){
        final ArrayList<BaseComponent> result = new ArrayList<>();
        result.add(new TextComponent("§Show All: "));
        final boolean verbose = profile.isShowPermissionNodes();
        final BaseComponent[] setYes = TextComponent.fromLegacyText(!verbose? "§7YES§r " : "§aYES§r ");
        final BaseComponent[] setNo = TextComponent.fromLegacyText(verbose? "§7NO§r " : "§aNO§r ");
        if (verbose) {
            result.addAll(Lists.newArrayList(setYes));
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick toggle setting."));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SettingsBase.ALIAS, SetHidden.ALIAS, "false"));
            Arrays.stream(setNo)
                    .peek(t->t.setHoverEvent(hoverEvent))
                    .peek(t->t.setClickEvent(clickEvent))
                    .forEach(result::add);
        }
        else {
            result.addAll(Lists.newArrayList(setNo));
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick toggle setting."));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SettingsBase.ALIAS, SetHidden.ALIAS, "true"));
            Arrays.stream(setYes)
                    .peek(t->t.setHoverEvent(hoverEvent))
                    .peek(t->t.setClickEvent(clickEvent))
                    .forEach(result::add);
        }
        return result.toArray(new BaseComponent[0]);
    }
}

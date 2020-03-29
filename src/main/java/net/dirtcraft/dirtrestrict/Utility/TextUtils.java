package net.dirtcraft.dirtrestrict.Utility;

import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import net.dirtcraft.dirtrestrict.Command.Editor.Settings.SetBypass;
import net.dirtcraft.dirtrestrict.Command.Editor.Settings.SetVerbose;
import net.dirtcraft.dirtrestrict.Command.Editor.Settings.SettingsBase;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.*;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TextUtils {
    public static TextComponent getRestrictionText(ItemKey item, Restriction entry, char main, char trim, char reason, boolean hasPerms){
        TextComponent textComponent = new TextComponent(getBaseText(item, entry, main, trim, reason));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(item, entry, hasPerms)));
        if (hasPerms) textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(EditRestriction.ALIAS, item)));
        return textComponent;
    }

    public static List<BaseComponent> getToggleLinks(ItemKey key, Restriction restriction, World world){
        ArrayList<BaseComponent> arr = new ArrayList<>();
        Arrays.stream(RestrictionTypes.values())
                .forEach(t->{
                    TextComponent text = new TextComponent("§6" + t.getName() + ": " + (restriction.isRestricted(t, world) ? "§4Banned" : "§2Allowed"));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to toggle.")));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleRestriction.ALIAS, key, t.toString())));
                    arr.add(text);
                });
        return arr;
    }

    public static BaseComponent[] getRemoveLinks(ItemKey key, Restriction restriction, World world){
        BaseComponent[] arr = new BaseComponent[key.data == null ? 3 : 4];
        {
            TextComponent text = new TextComponent("§3[§6Remove Ban§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick remove this entry.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(RemoveRestriction.ALIAS, key)));
            arr[0] = text;
        }
        {
            TextComponent text = new TextComponent(restriction.isDimsBlacklist()? " §3[§6Whitelist§3]" : " §3[§6Blacklist§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nSet the dim list to a " + (restriction.isDimsBlacklist()? "blacklist" : "whitelist"))));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleDimBlacklist.ALIAS, key)));
            arr[1] = text;
        }
        if (!restriction.hasDim(world.getUID())){
            TextComponent text = new TextComponent(!restriction.isDimsBlacklist()? " §3[§6Blacklist World§3]" : " §3[§6Whitelist World§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nAdds the current world to the " + (!restriction.isDimsBlacklist()? "blacklist" : "whitelist"))));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(AddDim.ALIAS, key, world.getName())));
            arr[2] = text;
        } else {
            TextComponent text = new TextComponent(" §3[§6Remove World§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nRemoves the current world from the " + (!restriction.isDimsBlacklist()? "blacklist" : "whitelist"))));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(RemoveDim.ALIAS, key, world.getName())));
            arr[2] = text;
        }
        if (key.data != null){
            TextComponent text = new TextComponent(" §3[§6Set Universal§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick make this entry ignore meta.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(SetUniversal.ALIAS, key)));
            arr[3] = text;
        }
        return arr;
    }

    public static BaseComponent getReason(ItemKey key, Restriction restriction){
        TextComponent text = new TextComponent("§6Ban Reason: §7" + restriction.getReason());
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to edit.")));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, getCommand(SetReason.ALIAS, key)));
        return text;
    }

    public static BaseComponent[] getMono(String s){
        BaseComponent[] arr = new BaseComponent[1];
        arr[0] = new TextComponent(s);
        return arr;
    }

    public static BaseComponent[] getLinks(ItemKey bannedItem){
        return getLinks(bannedItem, true, true);
    }

    public static BaseComponent[] getLinks(ItemKey bannedItem, boolean addEdit, boolean addList){
        ArrayList<BaseComponent> arr = new ArrayList<>();
        if (addEdit){
            final BaseComponent[] link = TextComponent.fromLegacyText(" §6§o[Edit]");
            final ClickEvent edit = new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(EditRestriction.ALIAS, bannedItem));
            final HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.getMono("§3§nClick to edit"));
            Arrays.stream(link).peek(l->l.setClickEvent(edit)).peek(l->l.setHoverEvent(hover)).forEach(arr::add);
        }
        if (addList){
            final BaseComponent[] link = TextComponent.fromLegacyText(" §6§o[List]");
            final ClickEvent edit = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/banneditems");
            final HoverEvent hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextUtils.getMono("§3§nClick to go back to ban list"));
            Arrays.stream(link).peek(l->l.setClickEvent(edit)).peek(l->l.setHoverEvent(hover)).forEach(arr::add);
        }
        return arr.toArray(new BaseComponent[0]);
    }

    private static String getBaseText(ItemKey item, Restriction entry, char main, char trim, char reason){
        StringBuilder stringBuilder = new StringBuilder();
        appendName(stringBuilder, item.getName(), main, trim);
        appendId(stringBuilder, item.item, item.data);
        appendReason(stringBuilder, entry.getReason(), reason, trim);
        return stringBuilder.toString();
    }

    private static void appendName(StringBuilder sb, String name, char color, char trim){
        sb.append("§").append(trim).append(" - §").append(color).append(name);
    }

    private static void appendId(StringBuilder sb, int i, Byte b){
        sb.append(" §r[§b").append(i);
        if (b != null) sb.append("§r:§3").append(b);
        sb.append("§r]");
    }

    private static void appendReason(StringBuilder sb, String reason, char color, char trim){
        if (reason != null && !reason.equals("") && !reason.matches("\\s")) sb.append("§").append(trim).append(" - §").append(color).append(reason);
    }

    private static String getCommand(String sub, ItemKey itemKey){
        final String item = itemKey.data == null? String.valueOf(itemKey.item) : itemKey.item + " " + itemKey.data;
        return "/dirtrestrict " + sub + " " + item;
    }

    private static String getCommand(String... args){
        return "/dirtrestrict " + Strings.join(args, " ");
    }

    private static String getCommand(String sub, ItemKey itemKey, String args){
        return getCommand(sub, itemKey) + " " + args;
    }

    public static BaseComponent getWorlds(Collection<UUID> set, ItemKey itemKey, boolean blackList){
        Server server = Bukkit.getServer();
        TextComponent x = new TextComponent(blackList? "§6Banned Worlds: §c" : "§6Permitted Worlds: §a");
        Iterator<UUID> uuidIterator = set.iterator();
        if (uuidIterator.hasNext()) x.addExtra("[");
        else x.addExtra("N/A");
        while (uuidIterator.hasNext()){
            x.addExtra(formatWorld(server.getWorld(uuidIterator.next()), itemKey, blackList ? 'c' : 'a'));
            if (uuidIterator.hasNext()) x.addExtra(", ");
            else x.addExtra("]");
        }
        return x;
    }

    private static BaseComponent formatWorld(World world, ItemKey itemKey, char color){
        TextComponent response = new TextComponent("§" + color + world.getName());
        response.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to  remove this world.")));
        response.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(RemoveDim.ALIAS, itemKey, world.getName())));
        return response;
    }

    private static BaseComponent[] getHover(ItemKey itemKey, Restriction restriction, boolean hasPerms){
        ArrayList<BaseComponent> hover = new ArrayList<>();
        hover.add(new TextComponent("§cBanned Methods:"));
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(RestrictionTypes.values())
                .forEach(t->hover.add(new TextComponent((i.getAndIncrement() % 4 == 0? "\n" : "§r, ") + "§6" + t.getName() + ": " + (restriction.isRestricted(t, null) ? "§4Banned" : "§2Allowed"))));
        hover.add(new TextComponent("\n§7id: " + itemKey.getUniqueIdentifier()));
        if (hasPerms) hover.add(new TextComponent("\n§3§nClick to edit this entry."));
        return hover.toArray(new BaseComponent[0]);
    }

    public static BaseComponent[] getBypassSlider(AdminProfile profile){
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

     public static BaseComponent[] getVerboseSlider(AdminProfile profile){
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
}

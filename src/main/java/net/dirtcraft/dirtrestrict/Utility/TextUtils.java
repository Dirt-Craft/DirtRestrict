package net.dirtcraft.dirtrestrict.Utility;

import net.dirtcraft.dirtrestrict.Command.SubCommands.*;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TextUtils {
    public static TextComponent getRestrictionText(ItemKey item, Restriction entry, char main, char trim, char reason, boolean hasPerms){
        TextComponent textComponent = new TextComponent(getBaseText(item, entry, main, trim, reason));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(item, entry, hasPerms)));
        if (hasPerms) textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(EditRestriction.ALIAS, item)));
        return textComponent;
    }

    public static List<BaseComponent> getToggleLinks(ItemKey key, Restriction restriction){
        ArrayList<BaseComponent> arr = new ArrayList<>();
        Arrays.stream(RestrictionTypes.values())
                .forEach(t->{
                    TextComponent text = new TextComponent("§6" + t.getName() + ": " + (restriction.isRestricted(t, null) ? "§4Banned" : "§2Allowed"));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to toggle.")));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleRestriction.ALIAS, key, t.toString())));
                    arr.add(text);
                });
        return arr;
    }

    public static BaseComponent[] getRemoveLinks(ItemKey key, Restriction restriction){
        BaseComponent[] arr = new BaseComponent[key.data == null ? 3 : 4];
        {
            TextComponent text = new TextComponent("§3[§6Remove Ban§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick remove this entry.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(RemoveRestriction.ALIAS, key)));
            arr[0] = text;
        }
        {
            TextComponent text = new TextComponent(restriction.isDimsBlacklist()? " §3[§6Whitelist Dims§3]" : " §3[§6Blacklist Dims§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nToggle the dims list to whitelist / blacklist.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleDimBlacklist.ALIAS, key)));
            arr[1] = text;
        }
        {
            TextComponent text = new TextComponent(restriction.isDimsBlacklist()? " §3[§6Blacklist World§3]" : " §3[§6Whitelist World§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nAdds the current world to the whitelist / blacklist.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleDimBlacklist.ALIAS, key)));
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

    private static String getCommand(String sub, ItemKey itemKey, String args){
        return getCommand(sub, itemKey) + " " + args;
    }

    public static BaseComponent getWorlds(Collection<UUID> set, ItemKey itemKey, boolean blackList){
        Server server = Bukkit.getServer();
        TextComponent x = new TextComponent(blackList? "§6Banned Worlds: [" : "Permitted Worlds: [");
        Iterator<UUID> uuidIterator = set.iterator();
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
}

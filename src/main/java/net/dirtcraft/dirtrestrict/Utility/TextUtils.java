package net.dirtcraft.dirtrestrict.Utility;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TextUtils {
    public static TextComponent getRestrictionText(ItemKey item, Restriction entry, char main, char trim, char reason, boolean hasPerms){
        TextComponent textComponent = new TextComponent(getBaseText(item, entry, main, trim, reason));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(item, entry, hasPerms)));
        if (hasPerms) textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(item)));
        return textComponent;
    }

    public static List<BaseComponent> getToggleLinks(ItemKey key, Restriction restriction){
        ArrayList<BaseComponent> arr = new ArrayList<>();
        Arrays.stream(RestrictionTypes.values())
                .forEach(t->{
                    TextComponent text = new TextComponent("§6" + t.getName() + " Banned: " + (restriction.isRestricted(t) ? "§4Yes" : "§2No"));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to toggle.")));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dirtrestrict toggle " + key.item + (key.data != null ? " " + key.data + " " : " ") + t));
                    arr.add(text);
                });
        return arr;
    }

    public static BaseComponent getRemoveLink(ItemKey key){
        TextComponent text = new TextComponent("§3[§6Remove Ban§3]");
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick remove this entry.")));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dirtrestrict remove " + key.item + (key.data != null ? " " + key.data + " " : " ")));
        return text;
    }

    public static BaseComponent getReason(ItemKey key, Restriction restriction){
        TextComponent text = new TextComponent("§6Ban Reason: §7" + restriction.getReason());
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to edit.")));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/dirtrestrict reason " + key.item + (key.data != null ? " " + key.data + " " : " ")));
        return text;
    }

    private static BaseComponent[] getMono(String s){
        BaseComponent[] arr = new BaseComponent[1];
        arr[0] = new TextComponent(s);
        return arr;
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

    private static String getCommand(ItemKey itemKey){
        return "/dirtrestrict edit " + itemKey.item + " " + itemKey.data;
    }

    private static BaseComponent[] getHover(ItemKey itemKey, Restriction restriction, boolean hasPerms){
        ArrayList<BaseComponent> hover = new ArrayList<>();
        hover.add(new TextComponent("§cBanned Methods:"));
        AtomicInteger i = new AtomicInteger();
        Arrays.stream(RestrictionTypes.values())
                .forEach(t->hover.add(new TextComponent((i.getAndIncrement() % 4 == 0? "\n" : "§r, ") + "§6" + t.getName() + ": " + (restriction.isRestricted(t) ? "§4Yes" : "§2No"))));
        hover.add(new TextComponent("\n§7id: " + itemKey.getUniqueIdentifier()));
        if (hasPerms) hover.add(new TextComponent("\n§3§nClick to edit this entry."));
        return hover.toArray(new BaseComponent[0]);
    }
}

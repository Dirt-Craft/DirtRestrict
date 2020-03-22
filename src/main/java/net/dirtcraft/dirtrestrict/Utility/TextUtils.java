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

public class TextUtils {
    public static TextComponent getRestrictionText(ItemKey item, Restriction entry, char main, char trim, char reason, boolean hasPerms){
        TextComponent textComponent = new TextComponent(getBaseText(item, entry, main, trim, reason));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(item, entry, hasPerms)));
        if (hasPerms) textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(item)));
        return textComponent;
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
        sb.append("§r]§");
    }

    private static void appendReason(StringBuilder sb, String reason, char color, char trim){
        if (reason != null) sb.append(trim).append(" - §").append(color).append(reason);
    }

    private static String getCommand(ItemKey itemKey){
        return "/dirtrestrict edit " + itemKey.item + " " + itemKey.data;
    }

    private static BaseComponent[] getHover(ItemKey itemKey, Restriction restriction, boolean hasPerms){
        ArrayList<BaseComponent> hover = new ArrayList<>();
        hover.add(new TextComponent("§cBanned Methods: "));
        Arrays.stream(RestrictionTypes.getTypes())
                .forEach(t->hover.add(new TextComponent("§6" + t + ": " + (restriction.isRestricted(t) ? "§4YES" : "§2NO"))));
        hover.add(new TextComponent("§7id: " + itemKey.getUniqueIdentifier()));
        if (hasPerms) hover.add(new TextComponent("§3Click to edit this entry."));
        return hover.toArray(new BaseComponent[0]);
    }
}

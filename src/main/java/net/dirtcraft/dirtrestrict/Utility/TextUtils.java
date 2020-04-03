package net.dirtcraft.dirtrestrict.Utility;

import joptsimple.internal.Strings;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.*;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public class TextUtils {

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

    public static BaseComponent[] getMono(String s){
        BaseComponent[] arr = new BaseComponent[1];
        arr[0] = new TextComponent(s);
        return arr;
    }

    public static String getCommand(String sub, ItemKey itemKey){
        final String item = itemKey.data == null? String.valueOf(itemKey.item) : itemKey.item + " " + itemKey.data;
        return "/dirtrestrict " + sub + " " + item;
    }

    public static String getCommand(String... args){
        return "/dirtrestrict " + Strings.join(args, " ");
    }

    public static String getCommand(String sub, ItemKey itemKey, String... args){
        return getCommand(sub, itemKey) + " " + Strings.join(args, " ");
    }
}

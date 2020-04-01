package net.dirtcraft.dirtrestrict.Command;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.dirtcraft.dirtrestrict.Command.Editor.SubCommands.EditRestriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.dirtcraft.dirtrestrict.Utility.Flipper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static net.dirtcraft.dirtrestrict.Utility.TextUtils.*;
import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;

public class BannedItemCommand implements CommandExecutor {
    public static final String ALIAS = "banneditems";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = args.length > 0? parseInt(args[0]).map(i->i-1).orElse(0) : 0;
        if (page < 0) return false;
        CompletableFuture.runAsync(()->runCommandAsync(sender, page));
        return true;
    }

    private void runCommandAsync(CommandSender sender, int pg){
        try {
            if (!(sender instanceof Player)) return;
            final int ENTRIES_PER_PAGE = 16;
            final Player player = (Player) sender;
            final boolean isStaff = player.hasPermission(Permission.PERMISSION_ADMIN);
            final Map<ItemKey, Restriction> restrictionMap = DirtRestrict.getInstance().getRestrictions().getRestrictions();
            final Flipper<Character> mainColor = new Flipper<>('6', 'e');
            final Flipper<Character> trimColor = new Flipper<>('3', 'b');
            final Flipper<Character> lastColor = new Flipper<>('8', '7');
            final Multimap<Integer, BaseComponent[]> entries = ArrayListMultimap.create();
            AtomicInteger i = new AtomicInteger();
            sender.sendMessage("§4§m====[§r §cDIRT§fCRAFT §4§m]=[§r §5BANNED ITEMS §4§m]====");
            restrictionMap.forEach(((itemKey, restriction) -> {
                final int page;
                if (restriction.isHidden() && !isStaff || (page = i.getAndIncrement() / ENTRIES_PER_PAGE) != pg) return;
                BaseComponent[] text = getRestrictionText(itemKey, restriction, mainColor.get(), trimColor.get(), lastColor.get(), isStaff);
                entries.put(page, text);
            }));
            final int pageMax = ((i.decrementAndGet()) / ENTRIES_PER_PAGE) + 1;
            entries.get(pg).forEach(player.spigot()::sendMessage);
            player.spigot().sendMessage(getPageFooter(pg+1, pageMax, "/banneditems"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private BaseComponent[] getPageFooter(int page, int max, String command){
        final BaseComponent[] arr = new BaseComponent[5];
        final TextComponent tNext = new TextComponent(page < max? "§3§l»§4" : "§c§l»");
        final TextComponent tBack = new TextComponent(page > 1? "§3§l«§4" : "§c§l«");
        if (page < max){
            tNext.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to view the next page.")));
            tNext.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (page + 1)));
        }
        if (page > 1){
            tBack.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to view the previous page.")));
            tBack.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command + " " + (page - 1)));
        }
        arr[0] = new TextComponent("§4§m=============[§4 ");
        arr[1] = tBack;
        arr[2] = new TextComponent(" §4" + page + "/" + max + " ");
        arr[3] = tNext;
        arr[4] = new TextComponent(" §4§m]=============");
        return arr;
    }

    private BaseComponent[] getRestrictionText(ItemKey item, Restriction entry, char main, char trim, char grey, boolean hasPerms){
        String reason = entry.getReason();
        String s = "§" + trim + " - §" + main + item.getName() +
                " §r[§b" + item.item + (item.data == null ? "§r]" : "§r:§3" + item.data + "§r]") +
                (reason != null && !reason.equals("") && !reason.matches("\\s") ? "§" + trim + " - §" + grey + reason : "");
        BaseComponent[] textComponent =  TextComponent.fromLegacyText(s);
        for (BaseComponent baseComponent : textComponent) {
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getHover(item, entry, hasPerms)));
            if (hasPerms) baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(EditRestriction.ALIAS, item)));
        }
        return textComponent;
    }

    private BaseComponent[] getHover(ItemKey itemKey, Restriction restriction, boolean hasPerms){
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

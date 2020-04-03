package net.dirtcraft.dirtrestrict.Command.Editor.SubCommands;

import net.dirtcraft.dirtrestrict.Command.SubCommand;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.ItemKey;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.Restriction;
import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import net.dirtcraft.dirtrestrict.Configuration.Permission;
import net.dirtcraft.dirtrestrict.DirtRestrict;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.dirtcraft.dirtrestrict.Utility.CommandUtils.*;
import static net.dirtcraft.dirtrestrict.Utility.TextUtils.*;

public class EditRestriction implements SubCommand {

    public static final String ALIAS = "Edit";
    @Override
    public String getName() {
        return ALIAS;
    }

    @Override
    public String getPermission() {
        return Permission.COMMAND_MODIFY_BASE;
    }

    @Override
    public List<String> getTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 0) {
            return DirtRestrict.getInstance().getRestrictions().getRestrictions().keySet()
                    .stream()
                    .map(ItemKey::getId)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        } else if (strings.length == 1){
            Optional<Material> optMaterial = parseMaterial(strings[0]);
            if (!optMaterial.isPresent()) return new ArrayList<>();
            int item = optMaterial.get().getId();
            return DirtRestrict.getInstance().getRestrictions().getRestrictions().keySet()
                    .stream()
                    .filter(key->key.item == item)
                    .map(ItemKey::getMeta)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1 || !(sender instanceof Player)) return false;
        final Player player = (Player) sender;
        final Optional<Material> optItem = parseMaterial(args[0]);
        final Optional<Byte> optByte = args.length > 1? parseByte(args[1]) : Optional.empty();
        if (!optItem.isPresent()) return false;

        final ItemKey itemKey = new ItemKey(optItem.get(), optByte.orElse(null));
        final Optional<Restriction> restriction = DirtRestrict.getInstance().getRestrictions().getRestriction(itemKey);

        if (!restriction.isPresent()) {
            sender.sendMessage("§4Error: §cThis item is not restricted!");
            return false;
        }

        sender.sendMessage("§4§m=============[§4§l EDITOR §4§m]=============");
        player.spigot().sendMessage(getRemoveLinks(itemKey, restriction.get()));
        sender.sendMessage("§6Name: §7" + itemKey.getName() + " §r[§b" + itemKey.item + (itemKey.data == null? "§r]" : "§r:§3" + itemKey.data + "§r]"));
        sender.sendMessage("§6ID: §7" + itemKey.getUniqueIdentifier());
        player.spigot().sendMessage(getWorlds(restriction.get().getDims(), itemKey, restriction.get(), restriction.get().isDimsBlacklist(), player.getWorld()));
        player.spigot().sendMessage(getReason(itemKey, restriction.get()));
        getToggleLinks(itemKey, restriction.get()).forEach(player.spigot()::sendMessage);
        sender.sendMessage("§4§m==================================");
        return false;
    }

    private static BaseComponent[] getRemoveLinks(ItemKey key, Restriction restriction){
        BaseComponent[] arr = new BaseComponent[key.data == null ? 3 : 4];
        {
            TextComponent text = new TextComponent("§3[§6Remove§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick remove this entry.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(RemoveRestriction.ALIAS, key)));
            arr[0] = text;
        }
        {
            TextComponent text = new TextComponent(restriction.isHidden()? " §3[§6Reveal§3]" : " §3[§6Hide§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to " + (restriction.isHidden()? "show" : "hide") + " this entry.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleHidden.ALIAS, key)));
            arr[1] = text;
        }
        {
            TextComponent text = new TextComponent(restriction.isRecipeDisabled()? " §3[§6Enable Recipe§3]" : " §3[§6Disable Recipe§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to " + (restriction.isHidden()? "enable" : "disable") + " this recipe.")));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleRecipe.ALIAS, key)));
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

    private static BaseComponent[] getWorlds(Collection<UUID> set, ItemKey key, Restriction restriction, boolean blackList, World world) {
        BaseComponent[] arr = new BaseComponent[3 + set.size() * 2];
        Server server = Bukkit.getServer();
        TextComponent label = new TextComponent(blackList ? "§6World §aWhitelist§6: §c" : "§6World §cBlacklist§6: §a");
        label.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nSet the dim list to a " + (restriction.isDimsBlacklist() ? "blacklist" : "whitelist"))));
        label.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleDimBlacklist.ALIAS, key)));
        arr[0] = label;
        if (!restriction.hasDim(world.getUID())){
            TextComponent text = new TextComponent("§3[§2+§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nAdds the current world to the " + (!restriction.isDimsBlacklist()? "blacklist" : "whitelist"))));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(AddDim.ALIAS, key, world.getName())));
            arr[arr.length-1] = text;
        } else {
            TextComponent text = new TextComponent("§3[§4-§3]");
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nRemoves the current world from the " + (!restriction.isDimsBlacklist()? "blacklist" : "whitelist"))));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(RemoveDim.ALIAS, key, world.getName())));
            arr[arr.length-1] = text;
        }

        final Iterator<UUID> uuidIterator = set.iterator();
        if (uuidIterator.hasNext()) arr[1] = new TextComponent("[");
        else {
            arr[1] = new TextComponent("N/A ");
        }
        int i = 1;
        while (uuidIterator.hasNext()) {
            BaseComponent worldBase = formatWorld(server.getWorld(uuidIterator.next()), key, blackList ? 'c' : 'a');
            BaseComponent joiner = new TextComponent(uuidIterator.hasNext()? ", " : "] ");
            arr[++i] = new TextComponent(worldBase);
            arr[++i] = new TextComponent(joiner);
        }
        return arr;
    }

    private static BaseComponent formatWorld(World world, ItemKey itemKey, char color){
        TextComponent response = new TextComponent("§" + color + world.getName());
        response.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to  remove this world.")));
        response.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(RemoveDim.ALIAS, itemKey, world.getName())));
        return response;
    }

    private static BaseComponent[] getReason(ItemKey key, Restriction restriction){
        List<BaseComponent> text = Arrays.asList(TextComponent.fromLegacyText("§6Ban Reason: §7" + restriction.getReason()));
        text.forEach(t->t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to edit."))));
        text.forEach(t->t.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, getCommand(SetReason.ALIAS, key))));
        return text.toArray(new BaseComponent[0]);
    }

    private static List<BaseComponent[]> getToggleLinks(ItemKey key, Restriction restriction){
        ArrayList<BaseComponent[]> arr = new ArrayList<>();
        final AtomicInteger i = new AtomicInteger(0);
        final int sz = RestrictionTypes.values().length;
        Arrays.stream(RestrictionTypes.values())
                .forEach(t->{
                    final int count = i.getAndIncrement();
                    final int index = count % 3;
                    String s = "§6" + t.getName() + ": " + (restriction.isRestricted(t) ? "§4Banned" : "§2Allowed");
                    TextComponent text = new TextComponent(String.format("%-22s", s));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, getMono("§3§nClick to toggle.")));
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, getCommand(ToggleRestriction.ALIAS, key, t.toString())));
                    if (index == 0){
                        final int allocate = Math.min(3, sz-count);
                        BaseComponent[] list = new BaseComponent[allocate];
                        list[index % 3] = text;
                        arr.add(list);
                    } else {
                        arr.get(arr.size()-1)[index] = text;
                    }
                });
        return arr;
    }
}

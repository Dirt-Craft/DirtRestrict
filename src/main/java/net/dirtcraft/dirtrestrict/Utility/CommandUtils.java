package net.dirtcraft.dirtrestrict.Utility;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;

public class CommandUtils {
    public static Optional<Material> parseMaterial(String s){
        try {
            if (s.matches("^\\d+$")) {
                return Optional.of(Material.getMaterial(Integer.parseInt(s)));
            } else {
                return Optional.of(Material.matchMaterial(s));
            }
        } catch (Exception ignored){
            return Optional.empty();
        }
    }

    public static Optional<Byte> parseByte(String s){
        try {
            return Optional.of(Byte.parseByte(s));
        } catch (Exception ignored){
            return Optional.empty();
        }
    }

    public static Optional<Integer> parseInt(String s){
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (Exception ignored){
            return Optional.empty();
        }
    }

    public static Optional<World> parseWorld(String s){
        Server server = Bukkit.getServer();
        try {
            if ((s.length() == 32 || s.length() == 36) && s.matches("(\\d{8}-?\\d{4}-?\\d{4}-?\\d{4}-?\\d{12})")){
                return Optional.ofNullable(server.getWorld(UUID.fromString(s)));
            }
            return Optional.ofNullable(server.getWorld(s));
        } catch (Exception ignored){
            return Optional.empty();
        }
    }

    public static Optional<RestrictionTypes> parseType(String s){
        return parseEnum(RestrictionTypes.class, s);
    }

    public static <T extends Enum<T>> Optional<T> parseEnum(Class<T> clazz, String s){
        try {
            return Optional.of(T.valueOf(clazz, s));
        } catch (Exception ignored){
            return Optional.empty();
        }
    }

    public static int parsePage(String s, int max){
        Optional<Integer> i = parseInt(s);
        return i.isPresent() && i.get() > 0 ? Math.min(i.get(), max) : 1;
    }
}

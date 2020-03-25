package net.dirtcraft.dirtrestrict.Utility;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionTypes;
import org.bukkit.Material;

import java.util.Optional;

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

    public static Optional<RestrictionTypes> parseType(String s){
        try {
            return Optional.of(RestrictionTypes.valueOf(s));
        } catch (Exception ignored){
            return Optional.empty();
        }
    }

    public static int parsePage(String s, int max){
        Optional<Integer> i = parseInt(s);
        return i.isPresent() && i.get() > 0 ? Math.min(i.get(), max) : 1;
    }
}

package net.dirtcraft.dirtrestrict.Utility;

import net.dirtcraft.dirtrestrict.Configuration.DataTypes.RestrictionType;
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

    public static Optional<RestrictionType> parseType(String s){
        try {
            return Optional.of(RestrictionType.valueOf(s));
        } catch (Exception ignored){
            return Optional.empty();
        }
    }
}

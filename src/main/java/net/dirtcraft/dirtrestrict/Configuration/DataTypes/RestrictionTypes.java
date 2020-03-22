package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

public enum RestrictionTypes implements RestrictionType {
    BREAK,
    PLACE,
    BREWING,
    SMELTING,
    CRAFTING,
    PICKUP,
    DROP,
    OWN,
    USE,
    CREATIVE;

    public static RestrictionType[] getTypes(){
        return values();
    }
}

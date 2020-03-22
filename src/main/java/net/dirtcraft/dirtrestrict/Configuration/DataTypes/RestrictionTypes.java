package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

public enum RestrictionTypes {
    BREAK   ("Break"),
    PLACE   ("Place"),
    BREWING ("Brew"),
    SMELTING("Smelt"),
    CRAFTING("Craft"),
    PICKUP  ("Pickup"),
    DROP    ("Drop"),
    OWN     ("Own"),
    USE     ("Use"),
    CREATIVE("Creative");
    final String name;
    RestrictionTypes(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
}

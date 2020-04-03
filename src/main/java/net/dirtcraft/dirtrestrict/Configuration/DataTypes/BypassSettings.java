package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import scala.util.control.Exception;

public enum  BypassSettings {
    RESPECT("Respect", '4'),
    NOTIFY ("Notify" , 'e'),
    IGNORE ("Ignore" , '2');
    final char color;
    final String formalName;
    BypassSettings(String formalName, char color){
        this.color = color;
        this.formalName = formalName;
    }

    public String getColorName(){
        return "§" + color + formalName;
    }
}

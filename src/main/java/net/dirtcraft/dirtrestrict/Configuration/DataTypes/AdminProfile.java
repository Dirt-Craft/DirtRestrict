package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.Sound;


@ConfigSerializable
public class AdminProfile {
    @Setting private BypassSettings bypassSetting = BypassSettings.NOTIFY;
    @Setting private boolean ShowPermissionNodes = false;
    @Setting private Sound sound = Sound.ZOMBIE_METAL;

    public boolean isShowPermissionNodes() {
        return ShowPermissionNodes;
    }

    public void setShowPermissionNodes(boolean showPermissionNodes) {
        ShowPermissionNodes = showPermissionNodes;
    }

    public BypassSettings getBypassSetting() {
        return bypassSetting;
    }

    public void setBypassSetting(BypassSettings bypassSetting) {
        this.bypassSetting = bypassSetting;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }
}

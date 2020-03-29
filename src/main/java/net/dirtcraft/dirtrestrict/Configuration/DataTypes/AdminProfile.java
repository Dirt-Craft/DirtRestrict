package net.dirtcraft.dirtrestrict.Configuration.DataTypes;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;


@ConfigSerializable
public class AdminProfile {
    @Setting private BypassSettings bypassSetting = BypassSettings.NOTIFY;
    @Setting private boolean ShowPermissionNodes = false;

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
}

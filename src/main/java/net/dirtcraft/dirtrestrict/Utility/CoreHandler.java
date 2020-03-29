package net.dirtcraft.dirtrestrict.Utility;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.FakePlayer;
import org.bukkit.entity.Player;

public interface CoreHandler {
    static CoreHandler INSTANCE = new Forge();

    boolean isFake(Player player);

    class Forge implements CoreHandler{

        @Override
        public boolean isFake(Player player) {
            boolean fake =  MinecraftServer.getServer().worldServers[0].getPlayerEntityByUUID(player.getUniqueId()) instanceof FakePlayer;
            System.out.println(false);
            return fake;
        }
    }
}

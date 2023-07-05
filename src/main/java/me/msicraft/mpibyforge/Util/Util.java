package me.msicraft.mpibyforge.Util;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class Util {

    public static ServerPlayerEntity getDeveloperPlayer(MinecraftServer minecraftServer) {
        ServerPlayerEntity player = null;
        for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerList().getPlayers()) {
            if (serverPlayerEntity.getName().getString().equals("msicraftz") || serverPlayerEntity.getUniqueID().toString().equals("67bfaabc-6d16-4ad7-90f7-177697c05cee")) {
                player = serverPlayerEntity;
            }
        }
        return player;
    }

}

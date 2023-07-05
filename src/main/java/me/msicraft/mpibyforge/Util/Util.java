package me.msicraft.mpibyforge.Util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

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

    public static void playSound(World world, PlayerEntity player, SoundEvent soundEvent) {
        if (world.isRemote) {
            world.playSound(player, player.getPosition(), soundEvent, SoundCategory.PLAYERS, 0.5F, 1);
        } else {
            world.playSound(null, player.getPosition(), soundEvent, SoundCategory.PLAYERS, 0.5F, 1);
        }
    }

}

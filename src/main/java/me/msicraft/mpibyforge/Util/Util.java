package me.msicraft.mpibyforge.Util;

import me.msicraft.mpibyforge.MPIByForge;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Util {

    public static String getDateByFormat(String pattern) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        try {
            formatter = DateTimeFormatter.ofPattern(pattern);
        } catch (IllegalArgumentException e) {
            //
        }
        return now.format(formatter);
    }

    public static String getTimeByFormat(String pattern) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        try {
            formatter = DateTimeFormatter.ofPattern(pattern);
        } catch (IllegalArgumentException e) {
            //
        }
        return now.format(formatter);
    }

    public static ServerPlayerEntity getDeveloperPlayer(MinecraftServer minecraftServer) {
        ServerPlayerEntity player = null;
        for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerList().getPlayers()) {
            if (serverPlayerEntity.getUniqueID().toString().equals("67bfaabc-6d16-4ad7-90f7-177697c05cee")) {
                player = serverPlayerEntity;
                break;
            }
        }
        return player;
    }

    public static boolean isDeveloperPlayer(PlayerEntity player) {
        boolean check = false;
        if (player.getUniqueID().toString().equals("67bfaabc-6d16-4ad7-90f7-177697c05cee")) {
            check = true;
        }
        return check;
    }

    public static void playSound(World world, PlayerEntity player, SoundEvent soundEvent) {
        if (world.isRemote) {
            world.playSound(player, player.getPosition(), soundEvent, SoundCategory.PLAYERS, 0.5F, 1);
        } else {
            world.playSound(null, player.getPosition(), soundEvent, SoundCategory.PLAYERS, 0.5F, 1);
        }
    }

    public static VillagerData getNitWitVillagerData(IVillagerType iVillagerType) {
        return new VillagerData(iVillagerType, VillagerProfession.NITWIT, 1);
    }

    public static void saveFileConfig(Map<String, Object> valuesMap) {
        MPIByForge.fileConfig.load();
        for (String s : valuesMap.keySet()) {
            Object o = valuesMap.get(s);
            MPIByForge.fileConfig.set(s, o);
            MPIByForge.fileConfig.save();
        }
        MPIByForge.fileConfig.close();
    }

}

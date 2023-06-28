package me.msicraft.mpibyforge.Event;

import me.msicraft.mpibyforge.Command.TeamSpawn;
import me.msicraft.mpibyforge.DataFile.TeamSpawnDataFile;
import me.msicraft.mpibyforge.MPIByForge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = MPIByForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class TeamClaimRelated {

    private static final Map<UUID, Long> disableMessageMap = new HashMap<>();
    private static final Map<String, List<ChunkPos>> teamClaimMap = new HashMap<>();

    public static void setTeamClaimToMap(String teamName, List<ChunkPos> chunkPosList) {
        teamClaimMap.put(teamName, chunkPosList);
    }

    public static List<ChunkPos> getTeamClaimChunks(String teamName) {
        List<ChunkPos> chunkPos = new ArrayList<>();
        if (teamClaimMap.containsKey(teamName)) {
            chunkPos = teamClaimMap.get(teamName);
        }
        return chunkPos;
    }

    public static void setVariables(MinecraftServer minecraftServer) {
        for (String teamName : minecraftServer.getScoreboard().getTeamNames()) {
            List<ChunkPos> chunkPos = TeamSpawnDataFile.getClaimChunks(teamName);
            setTeamClaimToMap(teamName, chunkPos);
            MPIByForge.getLogger().info("Load Team Claim: " + teamName);
        }
    }

    public static void saveToConfig() {
        for (String teamName : teamClaimMap.keySet()) {
            List<ChunkPos> chunkPosList = teamClaimMap.get(teamName);
            TeamSpawnDataFile.setTeamClaimChunks(teamName, chunkPosList);
            MPIByForge.getLogger().info("Save team claim: " + teamName);
        }
    }

    private static boolean inOtherTeamClaim(PlayerEntity player, ChunkPos blockChunkPos) {
        boolean check = false;
        String teamName = TeamSpawn.getTeamName(player);
        if (teamName != null) {
            for (String name : teamClaimMap.keySet()) {
                if (!name.equals(teamName)) {
                    List<ChunkPos> chunkPos = getTeamClaimChunks(name);
                    if (chunkPos.contains(blockChunkPos)) {
                        check = true;
                        break;
                    }
                }
            }
        }
        return check;
    }

    private static final double messageCoolDown = 5;

    @SubscribeEvent
    public static void inClaimInteract(PlayerInteractEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) e.getEntityLiving();
            ChunkPos blockChunkPos = player.getEntityWorld().getChunk(e.getPos()).getPos();
            if (inOtherTeamClaim(player, blockChunkPos)) {
                e.setCanceled(true);
                e.setResult(Event.Result.DENY);
                UUID uuid = player.getUniqueID();
                long time = System.currentTimeMillis();
                if (disableMessageMap.containsKey(uuid)) {
                    if (disableMessageMap.get(uuid) > time) {
                        player.sendMessage(new StringTextComponent("이곳은 다른 팀의 지역입니다."));
                        return;
                    }
                }
                long cd = (long) (time + (messageCoolDown * 1000));
                disableMessageMap.put(uuid, cd);
            }
        }
    }

}

package me.msicraft.mpibyforge.Event;

import me.msicraft.mpibyforge.Command.TeamSpawn;
import me.msicraft.mpibyforge.DataFile.TeamSpawnDataFile;
import me.msicraft.mpibyforge.MPIByForge;
import me.msicraft.mpibyforge.Util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = MPIByForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class TeamClaimRelated {

    private static final Map<UUID, Long> disableMessageMap = new HashMap<>();
    private static final Map<String, List<ChunkPos>> teamClaimMap = new HashMap<>();
    private static final List<ChunkPos> allTeamChunks = new ArrayList<>();

    public static void updateAllTeamChunks(String teamName) {
        if (teamClaimMap.containsKey(teamName)) {
            List<ChunkPos> list = teamClaimMap.get(teamName);
            allTeamChunks.addAll(list);
        }
    }

    public static void updateAllTeamChunks(List<ChunkPos> chunkPosList) {
        allTeamChunks.addAll(chunkPosList);
    }

    public static boolean hasTeam(String teamName) {
        return teamClaimMap.containsKey(teamName);
    }

    public static void setTeamClaimToMap(String teamName, List<ChunkPos> chunkPosList) {
        teamClaimMap.put(teamName, chunkPosList);
        updateAllTeamChunks(chunkPosList);
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
                if (Util.isDeveloperPlayer(player)) {
                    return;
                }
                e.setCanceled(true);
                e.setResult(Event.Result.DENY);
                UUID uuid = player.getUniqueID();
                long time = System.currentTimeMillis();
                if (disableMessageMap.containsKey(uuid)) {
                    if (disableMessageMap.get(uuid) > time) {
                        return;
                    }
                }
                long cd = (long) (time + (messageCoolDown * 1000));
                disableMessageMap.put(uuid, cd);
                player.sendMessage(new StringTextComponent("이곳은 다른 팀의 지역입니다."));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void preventExplode(ExplosionEvent.Start e) {
        Explosion explosion = e.getExplosion();
        ChunkPos chunkPos = new ChunkPos(new BlockPos(explosion.getPosition()));
        if (allTeamChunks.contains(chunkPos)) {
            e.setCanceled(true);
           // e.setResult(Event.Result.DENY);
        }
    }

}

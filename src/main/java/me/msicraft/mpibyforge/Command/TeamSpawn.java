package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.msicraft.mpibyforge.Event.EntityRelated;
import me.msicraft.mpibyforge.Event.TeamClaimRelated;
import me.msicraft.mpibyforge.a.Location;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;

public class TeamSpawn {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("teamspawn").then(Commands.argument("teamname", StringArgumentType.string())
                        .executes(command -> setTeamSpawn(command.getSource(), StringArgumentType.getString(command, "teamname"))))));
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("teamspawn").executes(command -> {
                    sendTeamSpawnLocation(command.getSource());
                    return 1;
                })));
        dispatcher.register(Commands.literal("spawn").executes(command -> spawn(command.getSource())));
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("spawn").then(Commands.argument("teamname", StringArgumentType.string())
                        .executes(command -> spawnTeam(command.getSource(), StringArgumentType.getString(command, "teamname"))))));
    }

    public static String getTeamName(PlayerEntity player) {
        String teamName = null;
        Team team = player.getTeam();
        if (team != null) {
            teamName = team.getName();
        }
        /*
        Scoreboard scoreboard = player.getWorldScoreboard();
        a1:
        for (String t : scoreboard.getTeamNames()) {
            for (String s : scoreboard.getTeam(t).getMembershipCollection()) {
                if (s.equals(player.getName().getString())) {
                    teamName = t;
                    break a1;
                }
            }
        }

         */
        return teamName;
    }

    public static int spawn(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            if (player.getEntityWorld().getDimension().getType() == DimensionType.OVERWORLD) {
                String teamName = getTeamName(player);
                Location location = EntityRelated.getTeamSpawnLocation(teamName);
                if (teamName != null && location != null) {
                    MinecraftServer minecraftServer = player.getServer();
                    if (minecraftServer != null) {
                        double x = location.getX() + 0.5;
                        double y = location.getY() + 0.15;
                        double z = location.getZ() + 0.5;
                        minecraftServer.getCommandManager().handleCommand(minecraftServer.getCommandSource(), "/execute in minecraft:overworld run tp " + player.getName().getString() + " " + x + " " + y + " " + z);
                        return 1;
                    }
                }
            } else {
                player.sendMessage(new StringTextComponent("차원 간 이동은 불가능 합니다"));
            }
        }
        return 0;
    }

    public static int spawnTeam(CommandSource commandSource, String teamName) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            Location location = EntityRelated.getTeamSpawnLocation(teamName);
            if (location != null) {
                MinecraftServer minecraftServer = player.getServer();
                if (minecraftServer != null) {
                    double x = location.getX() + 0.5;
                    double y = location.getY() + 0.15;
                    double z = location.getZ() + 0.5;
                    minecraftServer.getCommandManager().handleCommand(minecraftServer.getCommandSource(), "/execute in minecraft:overworld run tp " + player.getName().getString() + " " + x + " " + y + " " + z);
                    return 1;
                }
            }
        }
        return 0;
    }

    private static int setTeamSpawn(CommandSource commandSource, String teamName) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            Scoreboard scoreboard = player.getWorldScoreboard();
            if (scoreboard.getTeamNames().contains(teamName)) {
                int x = player.getPosition().getX();
                int y = player.getPosition().getY();
                int z = player.getPosition().getZ();
                Location location = new Location(x, y, z);
                EntityRelated.setTeamSpawn(teamName, location);
                List<ChunkPos> claimChunks = getSpawnClaims(commandSource);
                TeamClaimRelated.setTeamClaimToMap(teamName, claimChunks);
                return 1;
            }
        }
        return 0;
    }

    private static List<ChunkPos> getSpawnClaims(CommandSource commandSource) {
        List<ChunkPos> claims = new ArrayList<>();
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            ChunkPos chunkPos = player.getEntityWorld().getChunkAt(player.getPosition()).getPos();
            int x = chunkPos.x;
            int z = chunkPos.z;
            for (int a = (x-2); a<(x+2); a++) {
                for (int b = (z-2); b<(z+2); b++) {
                    ChunkPos pos = new ChunkPos(a, b);
                    claims.add(pos);
                }
            }
        }
        return claims;
    }

    private static void sendTeamSpawnLocation(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            Scoreboard scoreboard = player.getWorldScoreboard();
            for (String teamName : scoreboard.getTeamNames()) {
                Location location = EntityRelated.getTeamSpawnLocation(teamName);
                if (location != null) {
                    commandSource.sendFeedback(new StringTextComponent("Team: " + teamName +  " X|Y|Z " + location.getX() + ", " +
                            location.getY() + ", " + location.getZ()), false);
                }
            }
        }
    }

}

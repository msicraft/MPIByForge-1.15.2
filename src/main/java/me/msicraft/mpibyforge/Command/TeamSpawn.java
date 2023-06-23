package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.msicraft.mpibyforge.Event.EntityRelated;
import me.msicraft.mpibyforge.a.Location;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.StringTextComponent;

public class TeamSpawn {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.literal("teamspawn").then(Commands.argument("teamname", StringArgumentType.string())
                        .executes(command -> setTeamSpawn(command.getSource(), StringArgumentType.getString(command, "teamname"))))));
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.literal("teamspawn").executes(command -> {
                    sendTeamSpawnLocation(command.getSource());
                    return 1;
                })));
        dispatcher.register(Commands.literal("spawn").executes(command -> spawn(command.getSource())));
    }

    public static String getTeamName(PlayerEntity player) {
        String teamName = null;
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
        return teamName;
    }

    public static int spawn(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            String teamName = getTeamName(player);
            if (teamName != null) {
                Location location = EntityRelated.getTeamSpawnLocation(teamName);
                if (location != null) {
                    player.setPosition((location.getX()+0.5), (location.getY()+0.15), (location.getZ()+0.5));
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
            }
        }
        return 0;
    }

    private static void sendTeamSpawnLocation(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            Scoreboard scoreboard = player.getWorldScoreboard();
            for (String teamName : scoreboard.getTeamNames()) {
                Location location = EntityRelated.getTeamSpawnLocation(teamName);
                if (location != null) {
                    commandSource.sendFeedback(new StringTextComponent("Team: " + teamName + " X|Y|Z " + location.getX() + ", " +
                            location.getY() + ", " + location.getZ()), false);
                }
            }
        }
    }

}

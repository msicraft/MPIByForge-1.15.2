package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.msicraft.mpibyforge.Event.TeamClaimRelated;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class TeamInfo {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("teaminfo").then(Commands.argument("teamname", StringArgumentType.string())
                        .executes(command -> getTeamInfo(command.getSource(), StringArgumentType.getString(command, "teamname"))))));
        dispatcher.register(Commands.literal("teaminfo").executes(command -> getTeamInfo(command.getSource())));
    }

    public static int getTeamInfo(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            Team team = player.getTeam();
            if (team != null) {
                int totalSize = team.getMembershipCollection().size();
                List<String> teamMembers = new ArrayList<>(team.getMembershipCollection());
                commandSource.sendFeedback(new StringTextComponent("총 " + TextFormatting.GREEN + totalSize + TextFormatting.WHITE + " 명의 팀 구성원이 존재합니다."), false);
                commandSource.sendFeedback(new StringTextComponent("팀 구성원: " + teamMembers), false);
            } else {
                commandSource.sendFeedback(new StringTextComponent(TextFormatting.RED + "팀이 존재하지 않습니다."), false);
            }
        } else {
            commandSource.sendFeedback(new StringTextComponent(TextFormatting.RED + "플레이어만 사용할 수 있습니다."), false);
        }
        return 1;
    }

    public static int getTeamInfo(CommandSource commandSource, String teamName) {
        if (TeamClaimRelated.hasTeam(teamName)) {
            MinecraftServer minecraftServer = commandSource.getServer();
            ScorePlayerTeam scorePlayerTeam = minecraftServer.getScoreboard().getTeam(teamName);
            int totalSize = scorePlayerTeam.getMembershipCollection().size();
            List<String> teamMembers = new ArrayList<>(scorePlayerTeam.getMembershipCollection());
            commandSource.sendFeedback(new StringTextComponent("총 " + TextFormatting.GREEN + totalSize + TextFormatting.WHITE + " 명의 팀 구성원이 존재합니다."), false);
            commandSource.sendFeedback(new StringTextComponent("팀 구성원: " + teamMembers), false);
        } else {
            commandSource.sendFeedback(new StringTextComponent("존재 하지 않는 팀 입니다."), false);
        }
        return 1;
    }

}

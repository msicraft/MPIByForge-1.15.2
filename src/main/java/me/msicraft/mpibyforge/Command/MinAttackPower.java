package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import me.msicraft.mpibyforge.Event.EntityRelated;
import me.msicraft.mpibyforge.MPIByForge;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class MinAttackPower {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("minattackpower").then(Commands.argument("attackpower", FloatArgumentType.floatArg())
                        .executes(command -> setMinAttackPower(command.getSource(), FloatArgumentType.getFloat(command, "attackpower"))))));
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("minattackpower").executes(command -> getMinAttackPower(command.getSource()))));
    }

    private static int setMinAttackPower(CommandSource commandSource, float attackPower) {
        if (attackPower < 0) {
            commandSource.sendErrorMessage(new StringTextComponent("음수 값으로 설정 할 수 없습니다."));
            return 0;
        }
        MPIByForge.getLogger().info("MinAttackPower 값이 변경되었습니다. " + EntityRelated.getMinAttackPower() + " -> " + attackPower);
        EntityRelated.setMinAttackPower(attackPower);
        return 1;
    }

    private static int getMinAttackPower(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            player.sendMessage(new StringTextComponent("현재 MinAttackPower 값: " + EntityRelated.getMinAttackPower()));
        } else {
            commandSource.sendFeedback(new StringTextComponent("현재 MinAttackPower 값: " + EntityRelated.getMinAttackPower()), false);
        }
        return 1;
    }

}

package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.msicraft.mpibyforge.Event.EntityRelated;
import me.msicraft.mpibyforge.MPIByForge;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class PumpkinJuiceDrop {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("pumpkinjuicedroprate").then(Commands.argument("droprate", DoubleArgumentType.doubleArg())
                        .executes(command -> setPumpkinJuiceDroprate(command.getSource(), DoubleArgumentType.getDouble(command, "droprate"))))));
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("pumpkinjuicedroprate").executes(command -> {
                    command.getSource().sendFeedback(new StringTextComponent("현재 pumpkinjuicedroprate 값: " + EntityRelated.getPumpkinJuiceDropRate()), false);
                    return 1;
                })));
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("pumpkinjuicedroplevelrange").then(Commands.argument("range", IntegerArgumentType.integer())
                        .executes(command -> setPumpkinJuiceDropLevelRange(command.getSource(), IntegerArgumentType.getInteger(command, "range"))))));
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(4))
                .then(Commands.literal("pumpkinjuicedroplevelrange").executes(command -> {
                    command.getSource().sendFeedback(new StringTextComponent("현재 pumpkinjuicedroplevelrange 값: " + EntityRelated.getPumpkinJuiceDropLevelRange()), false);
                    return 1;
                })));
    }

    private static int setPumpkinJuiceDroprate(CommandSource commandSource, double droprate) {
        if (droprate < 0) {
            commandSource.sendErrorMessage(new StringTextComponent("음수 값으로 설정 할 수 없습니다."));
            return 0;
        }
        MPIByForge.getLogger().info("Pumpkinjuice drop rate 값이 변경되었습니다. " + EntityRelated.getPumpkinJuiceDropRate() + " -> " + droprate);
        EntityRelated.setPumpkinJuiceDropRate(droprate);
        return 1;
    }

    private static int setPumpkinJuiceDropLevelRange(CommandSource commandSource, int range) {
        if (range < 0) {
            commandSource.sendErrorMessage(new StringTextComponent("음수 값으로 설정 할 수 없습니다."));
            return 0;
        }
        MPIByForge.getLogger().info("Pumpkinjuice drop level range 값이 변경되었습니다. " + EntityRelated.getPumpkinJuiceDropLevelRange() + " -> " + range);
        EntityRelated.setPumpkinJuiceDropLevelRange(range);
        return 1;
    }

}

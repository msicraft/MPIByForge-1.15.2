package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.msicraft.mpibyforge.Event.EntityRelated;
import me.msicraft.mpibyforge.MPIByForge;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class SetNoDamageTick {

    private SetNoDamageTick() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.literal("nodamagetick").then(Commands.argument("tick", IntegerArgumentType.integer())
                        .executes(command -> setNoDamageTick(command.getSource(), IntegerArgumentType.getInteger(command, "tick"))))));
    }

    private static int setNoDamageTick(CommandSource commandSource, int tick) {
        if (tick < 0) {
            commandSource.sendErrorMessage(new StringTextComponent("음수 값으로 설정 할 수 없습니다."));
            return 0;
        }
        MPIByForge.getLogger().info("NoDamageTick 값이 변경되었습니다. " + EntityRelated.getNoDamageTick() + " -> " + tick);
        EntityRelated.setNoDamageTick(tick);
        return 1;
    }

}

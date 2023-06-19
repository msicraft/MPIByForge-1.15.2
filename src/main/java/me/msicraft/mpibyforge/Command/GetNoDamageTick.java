package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import me.msicraft.mpibyforge.Event.EntityRelated;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class GetNoDamageTick {

    private GetNoDamageTick() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("mpi").requires(cs -> cs.hasPermissionLevel(2))
                .then(Commands.literal("nodamagetick").executes(command -> {
                    command.getSource().sendFeedback(new StringTextComponent("현재 NoDamageTick 값: " + EntityRelated.getNoDamageTick()), false);
                    return 1;
                })));
    }

}

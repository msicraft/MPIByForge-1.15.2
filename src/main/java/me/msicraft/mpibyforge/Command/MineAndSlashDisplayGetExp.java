package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class MineAndSlashDisplayGetExp {

    private static final String tag = "MPI_MineAndSlash-DisplayExp";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("showexp").executes(command -> setTag(command.getSource())));
    }

    private static int setTag(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            if (player.getTags().contains(tag)) {
                player.removeTag(tag);
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "획득 경험치 표시가 비 활성화 되었습니다"));
            } else {
                player.addTag(tag);
                player.sendMessage(new StringTextComponent(TextFormatting.GREEN + "획득 경험치 표시가 활성화 되었습니다.(정확한 값이 아닌 대략적인 값이 표시됩니다.)"));
            }
        }
        return 0;
    }

    public static boolean hasTag(PlayerEntity player) {
        return player.getTags().contains(tag);
    }

}

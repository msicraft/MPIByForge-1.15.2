package me.msicraft.mpibyforge.Command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;

public class MainWorldSpawn {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("worldspawn").executes(command -> worldSpawn(command.getSource())));
    }

    public static int worldSpawn(CommandSource commandSource) {
        if (commandSource.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) commandSource.getEntity();
            if (player.getEntityWorld().getDimension().getType() == DimensionType.OVERWORLD) {
                BlockPos worldSpawn = player.getEntityWorld().getSpawnPoint();
                MinecraftServer minecraftServer = player.getServer();
                if (minecraftServer != null) {
                    double x = worldSpawn.getX() + 0.5;
                    double y = worldSpawn.getY() + 0.2;
                    double z = worldSpawn.getZ() + 0.5;
                    minecraftServer.getCommandManager().handleCommand(minecraftServer.getCommandSource(), "/execute in minecraft:overworld run tp " + player.getName().getString() + " " + x + " " + y + " " + z);
                    return 1;
                }
            } else {
                player.sendMessage(new StringTextComponent("차원 간 이동은 불가능 합니다"));
            }
        }
        return 0;
    }

}

package me.msicraft.mpibyforge.Event;

import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import me.msicraft.mpibyforge.MPIByForge;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;

@Mod.EventBusSubscriber(modid = MPIByForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class TickRelated {

    private static int counter = 0;
    @SubscribeEvent
    public static void applyGlowing(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            if (counter == 600) {
                counter = 0;
                MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
                List<ServerPlayerEntity> serverPlayerEntities = minecraftServer.getPlayerList().getPlayers();
                for (ServerPlayerEntity serverPlayerEntity : serverPlayerEntities) {
                    if (serverPlayerEntity.isAlive()) {
                        serverPlayerEntity.setGlowing(true);
                    }
                }
            } else {
                counter++;
            }
        }
    }

    @SubscribeEvent
    public static void changeDisplayName(PlayerEvent.NameFormat e) {
        PlayerEntity player = e.getPlayer();
        int level = Load.Unit(player).getLevel();
        ResourceLocation resourceLocation = player.getEntityWorld().getDimension().getType().getRegistryName();
        String dimensionName = "Unknown";
        if (resourceLocation != null) {
            dimensionName = resourceLocation.getPath();
        }
        String s = TextFormatting.GRAY + "[" + dimensionName + "]" + "[" + level + "] " + TextFormatting.WHITE + player.getName().getString();
        e.setDisplaynameComponent(new StringTextComponent(s));
    }

    private static int levelTickCount = 0;
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            PlayerEntity player = e.player;
            //UUID uuid = player.getUniqueID();
            if (levelTickCount == 300) {
                levelTickCount = 0;
                player.refreshDisplayName();
            } else {
                levelTickCount++;
            }
        }
    }

}

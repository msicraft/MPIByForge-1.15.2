package me.msicraft.mpibyforge;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.mojang.brigadier.CommandDispatcher;
import me.msicraft.mpibyforge.Command.MainWorldSpawn;
import me.msicraft.mpibyforge.Command.MinAttackPower;
import me.msicraft.mpibyforge.Command.NoDamageTick;
import me.msicraft.mpibyforge.Command.TeamSpawn;
import me.msicraft.mpibyforge.Config.ServerConfig;
import me.msicraft.mpibyforge.Event.EntityRelated;
import me.msicraft.mpibyforge.Event.TeamClaimRelated;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MPIByForge.MOD_ID)
public class MPIByForge {

    public static final String MOD_ID = "mpibyforge";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static Logger getLogger() {
        return LOGGER;
    }

    public static FileConfig fileConfig;
    public static String directoryPath;

    public MPIByForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setUpServer);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EntityRelated.class);
        MinecraftForge.EVENT_BUS.register(TeamClaimRelated.class);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "mpibyforge-server.toml");
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        MinecraftServer minecraftServer = event.getServer();
        CommandDispatcher<CommandSource> commandDispatcher = event.getCommandDispatcher();
        NoDamageTick.register(commandDispatcher);
        MinAttackPower.register(commandDispatcher);
        TeamSpawn.register(commandDispatcher);
        MainWorldSpawn.register(commandDispatcher);
        EntityRelated.setVariables(minecraftServer);
        TeamClaimRelated.setVariables(minecraftServer);
        LOGGER.info("MPIByForge Enabled");
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        EntityRelated.saveToConfig();
        TeamClaimRelated.saveToConfig();
        LOGGER.info("MPIByForge Disabled");
    }

    public void setUpServer(final FMLDedicatedServerSetupEvent e) {
        DedicatedServer dedicatedServer = e.getServerSupplier().get();
        ServerProperties serverProperties = dedicatedServer.getServerProperties();
        String configPath = dedicatedServer.getDataDirectory().toPath().toAbsolutePath() + File.separator + serverProperties.worldName +
                File.separator + "serverconfig" + File.separator + "mpibyforge-server.toml";
        fileConfig = FileConfig.of(configPath);
        directoryPath = dedicatedServer.getDataDirectory().toPath().toAbsolutePath().toString();
    }

}

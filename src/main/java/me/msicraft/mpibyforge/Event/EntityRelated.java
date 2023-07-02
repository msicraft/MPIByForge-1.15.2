package me.msicraft.mpibyforge.Event;

import com.robertx22.mine_and_slash.api.MineAndSlashEvents;
import com.robertx22.mine_and_slash.config.whole_mod_entity_configs.ModEntityConfig;
import com.robertx22.mine_and_slash.mmorpg.registers.common.CriteriaRegisters;
import com.robertx22.mine_and_slash.registry.SlashRegistry;
import com.robertx22.mine_and_slash.uncommon.capability.entity.EntityCap;
import com.robertx22.mine_and_slash.uncommon.capability.world.AntiMobFarmCap;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import me.msicraft.mpibyforge.Command.TeamSpawn;
import me.msicraft.mpibyforge.Config.ServerConfig;
import me.msicraft.mpibyforge.DataFile.TeamSpawnDataFile;
import me.msicraft.mpibyforge.MPIByForge;
import me.msicraft.mpibyforge.a.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@Mod.EventBusSubscriber(modid = MPIByForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class EntityRelated {

    private static int noDamageTick = 20;
    private static float minAttackPower = 0;
    private static double pumpkinJuiceDropRate = 0.000001;
    private static int pumpkinJuiceDropLevelRange = 10;

    private static final Map<String, Location> teamSpawnMap = new HashMap<>();

    public static void setTeamSpawn(String teamName, Location location) { teamSpawnMap.put(teamName, location); }
    public static Location getTeamSpawnLocation(String teamName) {
        Location location = null;
        if (teamSpawnMap.containsKey(teamName)) {
            location = teamSpawnMap.get(teamName);
        }
        return location;
    }

    public static int getNoDamageTick() { return noDamageTick; }
    public static float getMinAttackPower() { return minAttackPower; }
    public static double getPumpkinJuiceDropRate() { return pumpkinJuiceDropRate; }
    public static int getPumpkinJuiceDropLevelRange() { return pumpkinJuiceDropLevelRange; }

    public static void setVariables(MinecraftServer minecraftServer) {
        setNoDamageTick(ServerConfig.NODAMAGETICK.get());
        setMinAttackPower(ServerConfig.MINATTACKPOWER.get());
        setPumpkinJuiceDropRate(ServerConfig.PUMPKINJUICEDROPRATE.get());
        setPumpkinJuiceDropLevelRange(ServerConfig.PUMPKINJUICEDROPLEVELRANGE.get());
        for (String teamName : minecraftServer.getScoreboard().getTeamNames()) {
            Location location = TeamSpawnDataFile.getTeamSpawnLocation(teamName);
            if (location != null) {
                teamSpawnMap.put(teamName, location);
                MPIByForge.getLogger().info("Load team: " + teamName);
            }
        }
    }

    public static void setNoDamageTick(int tick) {
        if (tick < 0) {
            tick = 0;
        }
        noDamageTick = tick;
    }

    public static void setMinAttackPower(double attackPower) {
        if (attackPower < 0) {
            attackPower = 0;
        }
        minAttackPower = (float) attackPower;
    }

    public static void setPumpkinJuiceDropRate(double dropRate) {
        if (dropRate < 0) {
            dropRate = 0;
        }
        pumpkinJuiceDropRate = dropRate;
    }

    public static void setPumpkinJuiceDropLevelRange(int range) {
        if (range < 0) {
            range = 0;
        }
        pumpkinJuiceDropLevelRange = range;
    }

    public static void saveToConfig() {
        MPIByForge.fileConfig.load();
        MPIByForge.fileConfig.set("NoDamageTick", getNoDamageTick());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.set("MinAttackPower", getMinAttackPower());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.set("PumpkinJuiceDropRate", getPumpkinJuiceDropRate());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.set("PumpkinJuiceDropLevelRange", getPumpkinJuiceDropLevelRange());
        MPIByForge.fileConfig.save();
        for (String teamName : teamSpawnMap.keySet()) {
            Location location = teamSpawnMap.get(teamName);
            if (location != null) {
                MPIByForge.getLogger().info("Save team: " + teamName + " | " + location);
                TeamSpawnDataFile.setTeamSpawnLocation(teamName, location);
            }
        }
    }

    private static final List<DamageSource> ignoredDamageSources = Arrays.asList(DamageSource.DROWN, DamageSource.LAVA, DamageSource.HOT_FLOOR,
            DamageSource.STARVE, DamageSource.WITHER, DamageSource.SWEET_BERRY_BUSH, DamageSource.CACTUS, DamageSource.CRAMMING,
            DamageSource.IN_FIRE, DamageSource.IN_WALL, DamageSource.ON_FIRE);

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void changeNoDamageTick(LivingHurtEvent e) {
        if (!e.isCanceled()) {
            LivingEntity livingEntity = e.getEntityLiving();
            DamageSource damageSource = e.getSource();
            if (!ignoredDamageSources.contains(damageSource)) {
                livingEntity.hurtResistantTime = noDamageTick;
            }
        }
    }

    private static int counter = 0;

    @SubscribeEvent
    public static void applyGlowing(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            boolean check = false;
            if (counter == 400) {
                counter = 0;
                check = true;
            } else {
                counter++;
            }
            if (check) {
                MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
                List<ServerPlayerEntity> serverPlayerEntities = minecraftServer.getPlayerList().getPlayers();
                for (ServerPlayerEntity serverPlayerEntity : serverPlayerEntities) {
                    if (serverPlayerEntity.isAlive()) {
                        serverPlayerEntity.setGlowing(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void disableBoneMeal(BonemealEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void inventoryTotemOfUndying(LivingDeathEvent e) {
        if (e.getSource() != DamageSource.OUT_OF_WORLD) {
            LivingEntity livingEntity = e.getEntityLiving();
            if (livingEntity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) livingEntity;
                if (player.getHeldItemMainhand().getItem().equals(Items.TOTEM_OF_UNDYING) || player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
                    return;
                }
                int slot = -1;
                int count = 0;
                for (ItemStack itemStack : player.inventory.mainInventory) {
                    if (itemStack.getItem().equals(Items.TOTEM_OF_UNDYING)) {
                        slot = count;
                        break;
                    } else {
                        count++;
                    }
                }
                if (slot != -1) {
                    ItemStack totemStack = player.inventory.getStackInSlot(slot);
                    e.setCanceled(true);
                    float a = player.getMaxHealth() * 0.7f;
                    player.setHealth(a);
                    player.sendMessage(new StringTextComponent( TextFormatting.BOLD + "" + TextFormatting.RED + "불사의 토템이 사용되었습니다."));
                    totemStack.shrink(1);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applyAttackCoolDown(AttackEntityEvent e) {
        PlayerEntity player = e.getPlayer();
        float p = player.getCooledAttackStrength(1.0F);
        float p2 = minAttackPower;
        if (Float.compare(p, p2) > 0) {
            return;
        }
        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void babySpawn(BabyEntitySpawnEvent e) {
        if (Math.random() < 0.9) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void respawnPlayer(PlayerEvent.PlayerRespawnEvent e) {
        PlayerEntity player = e.getPlayer();
        if (player != null) {
            MinecraftServer minecraftServer = player.getServer();
            if (minecraftServer != null) {
                BlockPos blockPos = player.getBedLocation(DimensionType.OVERWORLD);
                if (blockPos == null) {
                    String teamName = TeamSpawn.getTeamName(player);
                    Location location = getTeamSpawnLocation(teamName);
                    if (teamName != null && location != null) {
                        double x = location.getX() + 0.5;
                        double y = location.getY() + 0.15;
                        double z = location.getZ() + 0.5;
                        minecraftServer.getCommandManager().handleCommand(minecraftServer.getCommandSource(), "/execute in minecraft:overworld run tp " + player.getName().getString() + " " + x + " " + y + " " + z);
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void vehicleRiding(EntityMountEvent e) {
        Entity entity = e.getEntityBeingMounted();
        if (entity != null) {
            Entity playerEntity = e.getEntityMounting();
            if (playerEntity instanceof PlayerEntity) {
                return;
            }
            e.setCanceled(true);
        }
    }

    private static int levelTickCount = 0;
    @SubscribeEvent
    public static void displayLevel(TickEvent.PlayerTickEvent e) {
        if (e.side == LogicalSide.SERVER && e.phase == TickEvent.Phase.END) {
            boolean check = false;
            if (levelTickCount == 600) {
                levelTickCount = 0;
                check = true;
            } else {
                levelTickCount++;
            }
            if (check) {
                PlayerEntity player = e.player;
                player.refreshDisplayName();
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void disableVillageDamage(MineAndSlashEvents.OnDmgDoneEvent e) {
        if (e.data.target instanceof VillagerEntity) {
            Set<String> tags = e.data.target.getTags();
            if (tags.contains("shop")) {
                e.data.damageMultiplier = 0;
            }
        }
    }

    private static ItemStack getPumpkinJuiceItemStack() {
        ItemStack itemStack = null;
        //PumpkinJuiceItem pumpkinJuiceItem = new PumpkinJuiceItem();
        ResourceLocation resourceLocation = new ResourceLocation("mmorpg:events/pumpkin_juice");
        Item pumpkinJuice = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (pumpkinJuice != null) {
            itemStack = new ItemStack(pumpkinJuice);
        }
        return itemStack;
    }

    private static ServerPlayerEntity getDeveloperPlayer(MinecraftServer minecraftServer) {
        ServerPlayerEntity player = null;
        for (ServerPlayerEntity serverPlayerEntity : minecraftServer.getPlayerList().getPlayers()) {
            if (serverPlayerEntity.getName().getString().equals("msicraftz") || serverPlayerEntity.getUniqueID().toString().equals("67bfaabc-6d16-4ad7-90f7-177697c05cee")) {
                player = serverPlayerEntity;
            }
        }
        return player;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void dropPumpkinJuice(LivingDeathEvent e) {
        if (Math.random() < getPumpkinJuiceDropRate()) {
            LivingEntity mobKilled = e.getEntityLiving();
            if (mobKilled.world.isRemote) {
                return;
            }
            if (!(mobKilled instanceof PlayerEntity)) {
                ItemStack itemStack = getPumpkinJuiceItemStack();
                if (itemStack != null) {
                    if (Load.hasUnit(mobKilled)) {
                        EntityCap.UnitData mobKilledData = Load.Unit(mobKilled);
                        Entity killerEntity = mobKilledData.getHighestDamageEntity(mobKilled);
                        if (killerEntity instanceof ServerPlayerEntity) {
                            ServerPlayerEntity player = (ServerPlayerEntity) killerEntity;
                            EntityCap.UnitData playerData = Load.Unit(player);

                            int mobLevel = mobKilledData.getLevel();
                            int playerLevel = playerData.getLevel();
                            int absLevelValue = Math.abs(mobLevel - playerLevel);
                            if (absLevelValue > getPumpkinJuiceDropLevelRange()) {
                                return;
                            }

                            CriteriaRegisters.DROP_LVL_PENALTY_TRIGGER.trigger(player, playerData, mobKilledData);

                            CriteriaRegisters.KILL_RARITY_MOB_TRIGGE.trigger(player, mobKilledData);

                            ModEntityConfig config = SlashRegistry.getEntityConfig(mobKilled, mobKilledData);

                            float loot_multi = (float) config.LOOT_MULTI;

                            if (loot_multi > 0) {
                                player.world.getCapability(AntiMobFarmCap.Data)
                                        .ifPresent(x -> x.onValidMobDeathByPlayer(mobKilled));
                            }
                            boolean check = false;
                            if (loot_multi > 0) {
                                BlockPos blockPos = mobKilled.getPosition();
                                ItemEntity item = new ItemEntity(player.world, blockPos.getX(), (blockPos.getY()+0.25), blockPos.getZ(), itemStack);
                                item.setLocationAndAngles(blockPos.getX(), blockPos.getY()+0.25, blockPos.getZ(), 0.0F, 0.0F);
                                check = player.world.addEntity(item);
                            }
                            MinecraftServer minecraftServer = player.getServer();
                            if (minecraftServer != null) {
                                ServerPlayerEntity developerEntity = getDeveloperPlayer(minecraftServer);
                                if (developerEntity != null) {
                                    developerEntity.sendMessage(new StringTextComponent(TextFormatting.GREEN + "호박 주스 드랍발생: " + check + " | " + loot_multi + " | " + getPumpkinJuiceDropRate() + " | " + " 관련 플레이어: " + player.getName().getString()));
                                }
                            }
                            MPIByForge.getLogger().info("호박 주스 드랍발생: " + check + " | " + loot_multi + " | " + getPumpkinJuiceDropRate() + " | " + " 관련 플레이어: " + player.getName().getString());
                        }
                    }
                }
            }
        }
    }

}

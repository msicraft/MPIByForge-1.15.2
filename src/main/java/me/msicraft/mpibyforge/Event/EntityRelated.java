package me.msicraft.mpibyforge.Event;

import com.robertx22.mine_and_slash.config.whole_mod_entity_configs.ModEntityConfig;
import com.robertx22.mine_and_slash.mmorpg.registers.common.CriteriaRegisters;
import com.robertx22.mine_and_slash.registry.SlashRegistry;
import com.robertx22.mine_and_slash.uncommon.capability.entity.EntityCap;
import com.robertx22.mine_and_slash.uncommon.capability.world.AntiMobFarmCap;
import com.robertx22.mine_and_slash.uncommon.datasaving.Load;
import me.msicraft.mpibyforge.Command.TeamSpawn;
import me.msicraft.mpibyforge.Config.ServerConfig;
import me.msicraft.mpibyforge.DataFile.PumpkinJuiceLogDataFile;
import me.msicraft.mpibyforge.DataFile.TeamSpawnDataFile;
import me.msicraft.mpibyforge.MPIByForge;
import me.msicraft.mpibyforge.Util.MineAndSlashUtil;
import me.msicraft.mpibyforge.Util.Util;
import me.msicraft.mpibyforge.a.Location;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MPIByForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class EntityRelated {

    private static int noDamageTick = 20;
    private static float minAttackPower = 0;
    private static double pumpkinJuiceDropRate = 0.00001;
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
        MPIByForge.getLogger().info("NoDamageTick Load: " + ServerConfig.NODAMAGETICK.get());
        setMinAttackPower(ServerConfig.MINATTACKPOWER.get());
        MPIByForge.getLogger().info("MinAttackPower Load: " + ServerConfig.MINATTACKPOWER.get());
        setPumpkinJuiceDropRate(ServerConfig.PUMPKINJUICEDROPRATE.get());
        MPIByForge.getLogger().info("PumpkinJuiceDropRate Load: " + ServerConfig.PUMPKINJUICEDROPRATE.get());
        setPumpkinJuiceDropLevelRange(ServerConfig.PUMPKINJUICEDROPLEVELRANGE.get());
        MPIByForge.getLogger().info("PumpkinJuiceDropLevelRange Load: " + ServerConfig.PUMPKINJUICEDROPLEVELRANGE.get());
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
        MPIByForge.getLogger().info("NoDamageTick Save: " + getNoDamageTick());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.set("MinAttackPower", getMinAttackPower());
        MPIByForge.getLogger().info("MinAttackPower Save: " + getMinAttackPower());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.set("PumpkinJuiceDropRate", getPumpkinJuiceDropRate());
        MPIByForge.getLogger().info("PumpkinJuiceDropRate Save: " + getPumpkinJuiceDropRate());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.set("PumpkinJuiceDropLevelRange", getPumpkinJuiceDropLevelRange());
        MPIByForge.getLogger().info("PumpkinJuiceDropLevelRange Save: " + getPumpkinJuiceDropLevelRange());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.close();
        for (String teamName : teamSpawnMap.keySet()) {
            Location location = teamSpawnMap.get(teamName);
            if (location != null) {
                MPIByForge.getLogger().info("Save team: " + teamName + " | " + location);
                TeamSpawnDataFile.setTeamSpawnLocation(teamName, location);
            }
        }
    }

    private static final List<DamageSource> ignoredDamageSources = Arrays.asList(DamageSource.DROWN, DamageSource.HOT_FLOOR,
            DamageSource.STARVE, DamageSource.WITHER, DamageSource.SWEET_BERRY_BUSH, DamageSource.CACTUS, DamageSource.CRAMMING, DamageSource.IN_WALL,
            DamageSource.LAVA, DamageSource.ON_FIRE, DamageSource.IN_FIRE);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void changeNoDamageTick(LivingHurtEvent e) {
        LivingEntity livingEntity = e.getEntityLiving();
        DamageSource damageSource = e.getSource();
        if (!ignoredDamageSources.contains(damageSource)) {
            livingEntity.hurtResistantTime = noDamageTick;
        }
    }

    @SubscribeEvent
    public static void disableBoneMeal(BonemealEvent e) {
        if (e.getEntityLiving() instanceof PlayerEntity) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
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
                    if (player instanceof ServerPlayerEntity) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
                        serverPlayerEntity.addStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                        CriteriaTriggers.USED_TOTEM.trigger(serverPlayerEntity, totemStack);
                    }
                    float a = player.getMaxHealth() * 0.75f;
                    player.setHealth(a);
                    player.sendMessage(new StringTextComponent( TextFormatting.BOLD + "" + TextFormatting.RED + "불사의 토템이 사용되었습니다."));
                    totemStack.shrink(1);
                    Util.playSound(player.world, player, SoundEvents.ITEM_TOTEM_USE);
                    player.world.setEntityState(player, (byte)35);
                    player.hurtResistantTime = 80;
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
                boolean check = false;
                BlockPos blockPos = player.getBedLocation(DimensionType.OVERWORLD);
                if (blockPos == null) {
                    check = true;
                }
                if (player.world.getDimension().getType() != DimensionType.OVERWORLD) {
                    check = true;
                }
                if (check) {
                    String teamName = TeamSpawn.getTeamName(player);
                    Location location = getTeamSpawnLocation(teamName);
                    if (teamName != null && location != null) {
                        double x = location.getX() + 0.5;
                        double y = location.getY() + 0.15;
                        double z = location.getZ() + 0.5;
                        minecraftServer.getCommandManager().handleCommand(minecraftServer.getCommandSource(), "/execute in minecraft:overworld run tp " + player.getName().getString() + " " + x + " " + y + " " + z);
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

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void dropPumpkinJuice(LivingDeathEvent e) {
        double randomP = Math.random();
        if (randomP < getPumpkinJuiceDropRate()) {
            LivingEntity mobKilled = e.getEntityLiving();
            if (mobKilled.world.isRemote) {
                return;
            }
            if (!(mobKilled instanceof PlayerEntity)) {
                ItemStack itemStack = MineAndSlashUtil.getPumpkinJuiceItemStack();
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
                            MinecraftServer minecraftServer = player.getServer();
                            ServerPlayerEntity developerPlayer = null;
                            if (minecraftServer != null) {
                                ServerPlayerEntity developerEntity = Util.getDeveloperPlayer(minecraftServer);
                                if (developerEntity != null) {
                                    developerPlayer = developerEntity;
                                }
                            }
                            if (absLevelValue > getPumpkinJuiceDropLevelRange()) {
                                MPIByForge.getLogger().info("레벨 페널티로인해 호박주스 드랍 실패: " + pumpkinJuiceDropRate + " | " + absLevelValue + " | Mob: " + mobLevel + " | Player: " + playerLevel + " | 관련 플레이어: " + player.getName().getString());
                                if (developerPlayer != null) {
                                    developerPlayer.sendMessage(new StringTextComponent(TextFormatting.GREEN + "레벨 페널티로인해 호박주스 드랍 실패: " + pumpkinJuiceDropRate + " | " + absLevelValue + " | Mob: " + mobLevel + " | Player: " + playerLevel + " | 관련 플레이어: " + player.getName().getString()));
                                }
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
                                ResourceLocation resourceLocation = player.world.getDimension().getType().getRegistryName();
                                String dimensionS = "Unknown";
                                if (resourceLocation != null) {
                                    dimensionS = resourceLocation.getPath();
                                }
                                String log = "[" + Util.getDateByFormat("yyyy/MM/dd") + " - " + Util.getTimeByFormat("HH시 mm분 ss초") + "] " +
                                        "드랍정보-> 월드: " + dimensionS + " | 엔티티: " + mobKilled.getType().getTranslationKey() + "/" + mobLevel +
                                        " | 플레이어: " + player.getName().getString() + "/" + playerLevel + " | 레벨차이: " + absLevelValue +
                                        " | LootMulti:" + loot_multi + " | 확률: " + getPumpkinJuiceDropRate() +
                                        " | 랜덤확률: " + randomP + " | 레벨범위: " + getPumpkinJuiceDropLevelRange();
                                if (PumpkinJuiceLogDataFile.addLog(log)) {
                                    MPIByForge.getLogger().info("성공적으로 호박주스 로그가 저장되었습니다");
                                } else {
                                    MPIByForge.getLogger().info("호박주스 로그가 저장되지 않았습니다");
                                }
                            }
                            if (developerPlayer != null) {
                                developerPlayer.sendMessage(new StringTextComponent(TextFormatting.GREEN + "호박 주스 드랍발생: " + check + " | " + loot_multi + " | " + getPumpkinJuiceDropRate() + " | " + " 관련 플레이어: " + player.getName().getString()));
                            }
                            MPIByForge.getLogger().info("호박 주스 드랍발생: " + check + " | " + loot_multi + " | " + getPumpkinJuiceDropRate() + " | " + " 관련 플레이어: " + player.getName().getString());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void disableDimensionChangeIgnorePlayer(EntityTravelToDimensionEvent e) {
        if (e.getEntity() instanceof PlayerEntity) {
            return;
        }
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void disableLibrarianTrade(PlayerInteractEvent.EntityInteractSpecific e) {
        if (e.getTarget() instanceof VillagerEntity) {
            VillagerEntity villagerEntity = (VillagerEntity) e.getTarget();
            if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
                villagerEntity.setVillagerData(Util.getNitWitVillagerData(villagerEntity.getVillagerData().getType()));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void disableLibrarianTrade2(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof VillagerEntity) {
            VillagerEntity villagerEntity = (VillagerEntity) e.getEntity();
            if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
                villagerEntity.setVillagerData(Util.getNitWitVillagerData(villagerEntity.getVillagerData().getType()));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void disableLibrarianTrade3(LivingSpawnEvent e) {
        if (e.getEntity() instanceof VillagerEntity) {
            VillagerEntity villagerEntity = (VillagerEntity) e.getEntity();
            if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
                villagerEntity.setVillagerData(Util.getNitWitVillagerData(villagerEntity.getVillagerData().getType()));
            }
        }
    }

}

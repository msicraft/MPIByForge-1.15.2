package me.msicraft.mpibyforge.Event;

import me.msicraft.mpibyforge.Config.ServerConfig;
import me.msicraft.mpibyforge.MPIByForge;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = MPIByForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class EntityRelated {

    private static int noDamageTick = 20;
    private static float minAttackPower = 0;

    public static int getNoDamageTick() { return noDamageTick; }
    public static float getMinAttackPower() { return minAttackPower; }

    public static void init() {
        setNoDamageTick(ServerConfig.NODAMAGETICK.get());
        setMinAttackPower(ServerConfig.MINATTACKPOWER.get());
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

    public static void saveToConfig() {
        MPIByForge.fileConfig.load();
        MPIByForge.fileConfig.set("NoDamageTick", getNoDamageTick());
        MPIByForge.fileConfig.save();
        MPIByForge.fileConfig.set("MinAttackPower", getMinAttackPower());
        MPIByForge.fileConfig.save();
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
                    totemStack.shrink(1);
                    player.sendMessage(new StringTextComponent("불사의 토템이 사용되었습니다."));
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

}

package me.msicraft.mpibyforge.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> NODAMAGETICK;
    public static final ForgeConfigSpec.ConfigValue<Double> MINATTACKPOWER;

    static {
        NODAMAGETICK = BUILDER.define("NoDamageTick", 20);
        MINATTACKPOWER = BUILDER.define("MinAttackPower", 0.0);
        SPEC = BUILDER.build();
    }

}

package me.msicraft.mpibyforge.a;

import net.minecraft.util.math.BlockPos;

public class Location {

    private double x;
    private double y;
    private double z;
    private BlockPos blockPos;

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        blockPos = new BlockPos(x, y, z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

}

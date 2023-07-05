package me.msicraft.mpibyforge.Util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class MineAndSlashUtil {

    public static ItemStack getPumpkinJuiceItemStack() {
        ItemStack itemStack = null;
        //PumpkinJuiceItem pumpkinJuiceItem = new PumpkinJuiceItem();
        ResourceLocation resourceLocation = new ResourceLocation("mmorpg:events/pumpkin_juice");
        Item pumpkinJuice = ForgeRegistries.ITEMS.getValue(resourceLocation);
        if (pumpkinJuice != null) {
            itemStack = new ItemStack(pumpkinJuice);
        }
        return itemStack;
    }

}

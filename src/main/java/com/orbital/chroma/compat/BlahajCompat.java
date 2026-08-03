package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlahajCompat {

    private static final int SHARK_BLUE = 0x4E7EA1;

    private BlahajCompat() {}

    public static void register() {
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (!id.getNamespace().equals("blahaj")) continue;

            Item item = entry.getValue();
            if (!(item instanceof net.minecraft.world.item.DyeableLeatherItem)) continue;
            if (ColorAPI.isDyeableItem(item)) continue;

            ColorAPI.registerCustomDyeableItem(item,
                    (stack, rgb) -> stack.getOrCreateTagElement("display").putInt("color", rgb),
                    stack -> {
                        if (stack.hasTag() && stack.getTag().contains("display")) {
                            var d = stack.getTag().getCompound("display");
                            if (d.contains("color")) return d.getInt("color");
                        }
                        return SHARK_BLUE;
                    });
        }
    }
}
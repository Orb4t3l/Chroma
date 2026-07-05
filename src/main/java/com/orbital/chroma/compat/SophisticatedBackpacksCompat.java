package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class SophisticatedBackpacksCompat {

    private static final String[] BACKPACKS = {
            "sophisticatedbackpacks:backpack",
            "sophisticatedbackpacks:iron_backpack",
            "sophisticatedbackpacks:gold_backpack",
            "sophisticatedbackpacks:diamond_backpack",
            "sophisticatedbackpacks:netherite_backpack"
    };

    private SophisticatedBackpacksCompat() {}

    public static void register() {
        for (String id : BACKPACKS) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item == null) continue;

            ColorAPI.registerCustomDyeableItem(
                    item,
                    (stack, rgb) -> {
                        var bet = stack.getOrCreateTagElement("BlockEntityTag");
                        bet.putInt("clothColor", rgb);
                        bet.putInt("borderColor", darken(rgb, 0.6f));
                    },
                    stack -> {
                        if (!stack.hasTag()) return 0x964B00;
                        var bet = stack.getTag().getCompound("BlockEntityTag");
                        return bet.contains("clothColor") ? bet.getInt("clothColor") : 0x964B00;
                    }
            );
        }
    }

    private static int darken(int rgb, float factor) {
        int r = Math.round(((rgb >> 16) & 0xFF) * factor);
        int g = Math.round(((rgb >> 8)  & 0xFF) * factor);
        int b = Math.round(( rgb        & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }
}
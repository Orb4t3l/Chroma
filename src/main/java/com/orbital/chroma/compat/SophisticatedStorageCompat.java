package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class SophisticatedStorageCompat {

    private static final String[] ITEMS = {
            "sophisticatedstorage:wooden_chest",
            "sophisticatedstorage:iron_chest",
            "sophisticatedstorage:gold_chest",
            "sophisticatedstorage:diamond_chest",
            "sophisticatedstorage:netherite_chest",
            "sophisticatedstorage:copper_chest",
            "sophisticatedstorage:wooden_barrel",
            "sophisticatedstorage:iron_barrel",
            "sophisticatedstorage:gold_barrel",
            "sophisticatedstorage:diamond_barrel",
            "sophisticatedstorage:netherite_barrel",
            "sophisticatedstorage:copper_barrel",
            "sophisticatedstorage:shulker_box",
    };

    private static final int DEFAULT_COLOR = 0x825432;

    private SophisticatedStorageCompat() {}

    public static void register() {
        for (String id : ITEMS) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item == null) continue;
            ColorAPI.registerCustomDyeableItem(item,
                    (stack, rgb) -> {
                        var bet = stack.getOrCreateTagElement("BlockEntityTag");
                        bet.putInt("clothColor", rgb);
                        bet.putInt("borderColor", darken(rgb, 0.6f));
                    },
                    stack -> {
                        if (!stack.hasTag()) return DEFAULT_COLOR;
                        var bet = stack.getTag().getCompound("BlockEntityTag");
                        return bet.contains("clothColor") ? bet.getInt("clothColor") : DEFAULT_COLOR;
                    });
        }
    }

    private static int darken(int rgb, float factor) {
        int r = Math.round(((rgb >> 16) & 0xFF) * factor);
        int g = Math.round(((rgb >> 8)  & 0xFF) * factor);
        int b = Math.round(( rgb        & 0xFF) * factor);
        return (r << 16) | (g << 8) | b;
    }
}
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

    private static final int DEFAULT_COLOR = 0x964B00;

    private SophisticatedBackpacksCompat() {}

    public static void register() {
        for (String id : BACKPACKS) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item == null) continue;

            ColorAPI.registerCustomDyeableItem(item,
                    (stack, rgb) -> {
                        // clothColor/borderColor live at the ROOT of the item's
                        // own tag, NOT nested under BlockEntityTag - confirmed
                        // from an actual in-game NBT dump.
                        var tag = stack.getOrCreateTag();
                        tag.putInt("clothColor", rgb);
                        tag.putInt("borderColor", darken(rgb, 0.6f));
                    },
                    stack -> {
                        if (stack.hasTag() && stack.getTag().contains("clothColor")) {
                            return stack.getTag().getInt("clothColor");
                        }
                        return DEFAULT_COLOR;
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
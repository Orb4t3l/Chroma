package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class QuarkCompat {

    private static final String[] COLORS = {
            "white","orange","magenta","light_blue","yellow","lime",
            "pink","gray","light_gray","cyan","purple","blue","brown","green","red","black"
    };

    private QuarkCompat() {}

    public static void register() {
        for (String color : COLORS) {
            registerDisplayColorItem("quark:" + color + "_rune",
                    DyeColor.byName(color, DyeColor.WHITE).getFireworkColor());
        }

        registerDisplayColorItem("quark:blank_rune", 0xAA00AA);

        Item elytra = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:elytra"));
        if (elytra != null) {
            ColorAPI.registerCustomDyeableItem(elytra,
                    (stack, rgb) -> stack.getOrCreateTagElement("display").putInt("color", rgb),
                    stack -> {
                        if (stack.hasTag() && stack.getTag().contains("display")) {
                            var d = stack.getTag().getCompound("display");
                            if (d.contains("color")) return d.getInt("color");
                        }
                        return 0xFFFFFF;
                    });
        }
    }

    private static void registerDisplayColorItem(String id, int defaultRgb) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) return;
        final int rgb = defaultRgb;
        ColorAPI.registerCustomDyeableItem(item,
                (stack, newRgb) -> stack.getOrCreateTagElement("display").putInt("color", newRgb),
                stack -> {
                    if (stack.hasTag() && stack.getTag().contains("display")) {
                        var d = stack.getTag().getCompound("display");
                        if (d.contains("color")) return d.getInt("color");
                    }
                    return rgb;
                });
    }
}
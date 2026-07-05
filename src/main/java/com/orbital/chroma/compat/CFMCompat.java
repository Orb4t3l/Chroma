package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class CFMCompat {

    private static final String[] COLORS = {
            "white","orange","magenta","light_blue","yellow","lime",
            "pink","gray","light_gray","cyan","purple","blue","brown","green","red","black"
    };
    private static final String[] TYPES = {
            "sofa","armchair","couch","dining_chair","bar_stool",
            "beach_chair","deckchair","work_stool","hammock",
            "cushion","pillow","blanket","curtain"
    };

    private CFMCompat() {}

    public static void register() {
        for (String type : TYPES) {
            for (String color : COLORS) {
                registerItem("cfm:" + color + "_" + type,
                        DyeColor.byName(color, DyeColor.WHITE).getFireworkColor());
            }
        }
    }

    private static void registerItem(String id, int defaultRgb) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) return;
        ColorAPI.registerCustomDyeableItem(
                item,
                (stack, rgb) -> stack.getOrCreateTagElement("display").putInt("color", rgb),
                stack -> {
                    if (stack.hasTag() && stack.getTag().contains("display")) {
                        var d = stack.getTag().getCompound("display");
                        if (d.contains("color")) return d.getInt("color");
                    }
                    return defaultRgb;
                }
        );
    }
}
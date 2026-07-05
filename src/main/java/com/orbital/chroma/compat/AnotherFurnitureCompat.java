package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class AnotherFurnitureCompat {

    private static final String[] COLORS = {
            "white","orange","magenta","light_blue","yellow","lime",
            "pink","gray","light_gray","cyan","purple","blue","brown","green","red","black"
    };
    private static final String[] TYPES = { "cushion", "pillow" };

    private AnotherFurnitureCompat() {}

    public static void register() {
        for (String type : TYPES) {
            registerItem("another_furniture:" + type, 0xFFFFFF);
            for (String color : COLORS) {
                registerItem("another_furniture:" + color + "_" + type,
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
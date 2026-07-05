package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class CreateCompat {

    private static final String[] COLORS = {
            "white","orange","magenta","light_blue","yellow","lime",
            "pink","gray","light_gray","cyan","purple","blue","brown","green","red","black"
    };

    private CreateCompat() {}

    public static void register() {
        for (String color : COLORS) {
            String id = "create:" + color + "_seat";
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item == null) continue;
            float[] tex = DyeColor.byName(color, DyeColor.WHITE).getTextureDiffuseColors();
            int defaultRgb = ((int)(tex[0]*255) << 16) | ((int)(tex[1]*255) << 8) | (int)(tex[2]*255);
            final int rgb = defaultRgb;
            ColorAPI.registerCustomDyeableItem(
                    item,
                    (stack, newRgb) -> stack.getOrCreateTagElement("display").putInt("color", newRgb),
                    stack -> {
                        if (stack.hasTag() && stack.getTag().contains("display")) {
                            var d = stack.getTag().getCompound("display");
                            if (d.contains("color")) return d.getInt("color");
                        }
                        return rgb;
                    }
            );
        }
    }
}
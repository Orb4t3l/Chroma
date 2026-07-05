package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public final class BlahajCompat {

    private static final int SHARK_BLUE = 0x4E7EA1;

    private static final String[] PLUSHIES = {
            "blahaj:blahaj",
            "blahaj:klappar_haj",
            "blahaj:blavingad",
            "blahaj:bread_pillow"
    };

    private BlahajCompat() {}

    public static void register() {
        for (String id : PLUSHIES) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            if (item == null) continue;
            ColorAPI.registerCustomDyeableItem(
                    item,
                    (stack, rgb) -> stack.getOrCreateTagElement("display").putInt("color", rgb),
                    stack -> {
                        if (stack.hasTag() && stack.getTag().contains("display")) {
                            var d = stack.getTag().getCompound("display");
                            if (d.contains("color")) return d.getInt("color");
                        }
                        return SHARK_BLUE;
                    }
            );
        }
    }
}
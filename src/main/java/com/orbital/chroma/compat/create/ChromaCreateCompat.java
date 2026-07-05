package com.orbital.chroma.compat.create;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Mod("chroma_create_compat")
public class ChromaCreateCompat {

    public static final String MOD_ID = "chroma_create_compat";

    private static final String[] DYE_COLORS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime",
            "pink", "gray", "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };

    @SuppressWarnings("removal")
    public ChromaCreateCompat() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("create")) return;

        event.enqueueWork(() -> {
            for (String color : DYE_COLORS) {
                String id = "create:" + color + "_seat";
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                if (item == null) continue;

                DyeColor dyeColor = DyeColor.byName(color, DyeColor.WHITE);
                float[] components = dyeColor.getTextureDiffuseColors();
                int defaultRgb = ((int)(components[0] * 255) << 16)
                        | ((int)(components[1] * 255) << 8)
                        | (int)(components[2] * 255);

                final Item targetItem = item;
                final int rgb = defaultRgb;

                BiConsumer<ItemStack, Integer> setter = (stack, newRgb) ->
                        stack.getOrCreateTagElement("display").putInt("color", newRgb);

                Function<ItemStack, Integer> getter = (stack) -> {
                    if (stack.hasTag() && stack.getTag().contains("display")) {
                        var display = stack.getTag().getCompound("display");
                        if (display.contains("color")) return display.getInt("color");
                    }
                    return rgb;
                };

                ColorAPI.registerCustomDyeableItem(item, setter, getter);
            }
        });
    }
}
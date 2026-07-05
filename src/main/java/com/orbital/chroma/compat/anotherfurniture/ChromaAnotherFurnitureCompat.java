package com.orbital.chroma.compat.anotherfurniture;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("chroma_anotherfurniture_compat")
public class ChromaAnotherFurnitureCompat {

    public static final String MOD_ID = "chroma_anotherfurniture_compat";

    private static final String[] DYE_COLORS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime",
            "pink", "gray", "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };

    private static final String[] DYEABLE_ITEM_SUFFIXES = {
            "cushion", "pillow"
    };

    @SuppressWarnings("removal")
    public ChromaAnotherFurnitureCompat() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("another_furniture")) return;

        event.enqueueWork(() -> {
            for (String suffix : DYEABLE_ITEM_SUFFIXES) {
                for (String color : DYE_COLORS) {
                    registerColoredItem("another_furniture:" + color + "_" + suffix,
                            DyeColor.byName(color, DyeColor.WHITE).getFireworkColor());
                }
                registerColoredItem("another_furniture:" + suffix, 0xFFFFFF);
            }
        });
    }

    private static void registerColoredItem(String id, int defaultRgb) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        if (item == null) return;

        ColorAPI.registerCustomDyeableItem(
                item,
                (stack, rgb) -> stack.getOrCreateTagElement("display").putInt("color", rgb),
                (stack) -> {
                    if (stack.hasTag() && stack.getTag().contains("display")) {
                        var display = stack.getTag().getCompound("display");
                        if (display.contains("color")) return display.getInt("color");
                    }
                    return defaultRgb;
                }
        );
    }
}
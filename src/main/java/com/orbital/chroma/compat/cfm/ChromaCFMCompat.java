package com.orbital.chroma.compat.cfm;

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

@Mod("chroma_cfm_compat")
public class ChromaCFMCompat {

    public static final String MOD_ID = "chroma_cfm_compat";

    private static final String[] DYE_COLORS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime",
            "pink", "gray", "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };

    private static final String[] COLORED_FURNITURE = {
            "sofa", "armchair", "couch", "dining_chair", "bar_stool",
            "beach_chair", "deckchair", "work_stool", "hammock",
            "cushion", "pillow", "blanket", "curtain"
    };

    @SuppressWarnings("removal")
    public ChromaCFMCompat() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("cfm")) return;

        event.enqueueWork(() -> {
            for (String furniture : COLORED_FURNITURE) {
                for (String color : DYE_COLORS) {
                    int defaultRgb = DyeColor.byName(color, DyeColor.WHITE).getFireworkColor();

                    String itemId = "cfm:" + color + "_" + furniture;
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                    if (item == null) continue;

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
            }
        });
    }
}
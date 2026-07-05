package com.orbital.chroma.compat.blahaj;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("chroma_blahaj_compat")
public class ChromaBlahajCompat {

    public static final String MOD_ID = "chroma_blahaj_compat";

    @SuppressWarnings("removal")
    public ChromaBlahajCompat() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("blahaj")) return;

        event.enqueueWork(() -> {
            registerPlushie("blahaj:blahaj");
            registerPlushie("blahaj:klappar_haj");
            registerPlushie("blahaj:blavingad");
            registerPlushie("blahaj:bread_pillow");
        });
    }

    private static void registerPlushie(String id) {
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
                    return 0x4E7EA1;
                }
        );
    }
}
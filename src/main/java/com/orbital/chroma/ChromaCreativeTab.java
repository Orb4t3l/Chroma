package com.orbital.chroma;

import com.orbital.chroma.registry.ChromaItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChromaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChromaCreativeTab {

    private ChromaCreativeTab() {}

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ChromaItems.DYEING_TABLE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
            event.accept(ChromaItems.CHROMA_WOOL.get());
            event.accept(ChromaItems.CHROMA_CARPET.get());
            event.accept(ChromaItems.CHROMA_CONCRETE.get());
            event.accept(ChromaItems.CHROMA_CONCRETE_POWDER.get());
            event.accept(ChromaItems.CHROMA_TERRACOTTA.get());
            event.accept(ChromaItems.CHROMA_STAINED_GLASS.get());
            event.accept(ChromaItems.CHROMA_BANNER.get());
        }
    }
}
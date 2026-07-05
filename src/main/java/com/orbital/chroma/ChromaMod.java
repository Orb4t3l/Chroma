package com.orbital.chroma;

import com.orbital.chroma.compat.CompatManager;
import com.orbital.chroma.network.ChromaNetwork;
import com.orbital.chroma.registry.ChromaBlockEntities;
import com.orbital.chroma.registry.ChromaBlocks;
import com.orbital.chroma.registry.ChromaItems;
import com.orbital.chroma.registry.ChromaMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ChromaMod.MOD_ID)
public class ChromaMod {

    public static final String MOD_ID = "chroma";

    @SuppressWarnings("removal")
    public ChromaMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ChromaBlocks.REGISTRY.register(bus);
        ChromaItems.REGISTRY.register(bus);
        ChromaBlockEntities.REGISTRY.register(bus);
        ChromaMenus.REGISTRY.register(bus);

        ChromaNetwork.register();

        bus.addListener(CompatManager::init);
    }
}
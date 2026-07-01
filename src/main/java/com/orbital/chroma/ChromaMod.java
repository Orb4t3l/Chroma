package com.orbital.chroma;

import com.orbital.chroma.network.ChromaNetwork;
import com.orbital.chroma.registry.ChromaBlockEntities;
import com.orbital.chroma.registry.ChromaBlocks;
import com.orbital.chroma.registry.ChromaItems;
import com.orbital.chroma.registry.ChromaMenus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

@Mod(ChromaMod.MOD_ID)
public class ChromaMod {

    public static final String MOD_ID = "chroma";

    public ChromaMod(IEventBus modEventBus) {
        ChromaBlocks.REGISTRY.register(modEventBus);
        ChromaItems.REGISTRY.register(modEventBus);
        ChromaBlockEntities.REGISTRY.register(modEventBus);
        ChromaMenus.REGISTRY.register(modEventBus);
        ChromaNetwork.register();
    }
}
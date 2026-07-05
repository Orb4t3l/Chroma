package com.orbital.chroma.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class CompatManager {

    private CompatManager() {}

    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Explicit compat runs first so specialised NBT paths take priority
            // over anything the auto-scanner would register with display.color
            if (ModList.get().isLoaded("blahaj")) BlahajCompat.register();
            if (ModList.get().isLoaded("handcrafted")) HandcraftedCompat.register();
            if (ModList.get().isLoaded("another_furniture")) AnotherFurnitureCompat.register();
            if (ModList.get().isLoaded("cfm")) CFMCompat.register();
            if (ModList.get().isLoaded("create")) CreateCompat.register();
            if (ModList.get().isLoaded("sophisticatedbackpacks")) SophisticatedBackpacksCompat.register();
            if (ModList.get().isLoaded("sophisticatedstorage")) SophisticatedStorageCompat.register();
            if (ModList.get().isLoaded("supplementaries")) SupplementariesCompat.register();
            if (ModList.get().isLoaded("mcwfurnitures")) MacawsFurnitureCompat.register();
            if (ModList.get().isLoaded("quark")) QuarkCompat.register();
            if (ModList.get().isLoaded("ae2"))     AE2Compat.register();

            AutoDyeScanner.scan();
        });
    }
}
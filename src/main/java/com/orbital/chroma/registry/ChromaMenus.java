package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.menu.DyeingTableMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChromaMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ChromaMod.MOD_ID);

    public static final RegistryObject<MenuType<DyeingTableMenu>> DYEING_TABLE = REGISTRY.register(
            "dyeing_table",
            () -> IForgeMenuType.create((containerId, inventory, buffer) ->
                    new DyeingTableMenu(containerId, inventory, buffer.readBlockPos())));

    private ChromaMenus() {
    }
}
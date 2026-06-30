package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChromaItems {

    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ChromaMod.MOD_ID);


    //i do not care that dyeing is spelt wrong, suck my balls
    public static final RegistryObject<Item> DYEING_TABLE = REGISTRY.register("dyeing_table",
            () -> new BlockItem(ChromaBlocks.DYEING_TABLE.get(), new Item.Properties()));

    private ChromaItems() {
    }
}
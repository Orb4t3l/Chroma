package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChromaItems {

    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ITEMS, ChromaMod.MOD_ID);

    public static final RegistryObject<Item> DYEING_TABLE = REGISTRY.register("dyeing_table",
            () -> new BlockItem(ChromaBlocks.DYEING_TABLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHROMA_WOOL = REGISTRY.register("chroma_wool",
            () -> new BlockItem(ChromaBlocks.CHROMA_WOOL.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHROMA_CARPET = REGISTRY.register("chroma_carpet",
            () -> new BlockItem(ChromaBlocks.CHROMA_CARPET.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHROMA_CONCRETE = REGISTRY.register("chroma_concrete",
            () -> new BlockItem(ChromaBlocks.CHROMA_CONCRETE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHROMA_CONCRETE_POWDER = REGISTRY.register("chroma_concrete_powder",
            () -> new BlockItem(ChromaBlocks.CHROMA_CONCRETE_POWDER.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHROMA_TERRACOTTA = REGISTRY.register("chroma_terracotta",
            () -> new BlockItem(ChromaBlocks.CHROMA_TERRACOTTA.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHROMA_STAINED_GLASS = REGISTRY.register("chroma_stained_glass",
            () -> new BlockItem(ChromaBlocks.CHROMA_STAINED_GLASS.get(), new Item.Properties()));

    public static final RegistryObject<Item> CHROMA_BANNER = REGISTRY.register("chroma_banner",
            () -> new BannerItem(ChromaBlocks.CHROMA_BANNER.get(), ChromaBlocks.CHROMA_WALL_BANNER.get(),
                    new Item.Properties().stacksTo(16)));

    private ChromaItems() {}
}
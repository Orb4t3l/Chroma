package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.block.ChromaBannerBlock;
import com.orbital.chroma.block.ChromaCarpetBlock;
import com.orbital.chroma.block.ChromaDyeableBlock;
import com.orbital.chroma.block.ChromaStainedGlassBlock;
import com.orbital.chroma.block.ChromaWallBannerBlock;
import com.orbital.chroma.block.DyeingTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChromaBlocks {

    public static final DeferredRegister<Block> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ChromaMod.MOD_ID);

    public static final RegistryObject<Block> DYEING_TABLE = REGISTRY.register("dyeing_table",
            () -> new DyeingTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD).strength(2.5f).noOcclusion()));

    public static final RegistryObject<Block> CHROMA_WOOL = REGISTRY.register("chroma_wool",
            () -> new ChromaDyeableBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(0.8f).sound(SoundType.WOOL),
                    () -> ChromaBlockEntities.CHROMA_WOOL.get()));

    public static final RegistryObject<Block> CHROMA_CARPET = REGISTRY.register("chroma_carpet",
            () -> new ChromaCarpetBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).strength(0.1f).sound(SoundType.WOOL),
                    () -> ChromaBlockEntities.CHROMA_CARPET.get()));

    public static final RegistryObject<Block> CHROMA_CONCRETE = REGISTRY.register("chroma_concrete",
            () -> new ChromaDyeableBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.8f)
                            .requiresCorrectToolForDrops().sound(SoundType.STONE),
                    () -> ChromaBlockEntities.CHROMA_CONCRETE.get()));

    public static final RegistryObject<Block> CHROMA_CONCRETE_POWDER = REGISTRY.register("chroma_concrete_powder",
            () -> new ChromaDyeableBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(0.5f).sound(SoundType.SAND),
                    () -> ChromaBlockEntities.CHROMA_CONCRETE_POWDER.get()));

    public static final RegistryObject<Block> CHROMA_TERRACOTTA = REGISTRY.register("chroma_terracotta",
            () -> new ChromaDyeableBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.25f)
                            .requiresCorrectToolForDrops().sound(SoundType.STONE),
                    () -> ChromaBlockEntities.CHROMA_TERRACOTTA.get()));

    public static final RegistryObject<Block> CHROMA_STAINED_GLASS = REGISTRY.register("chroma_stained_glass",
            () -> new ChromaStainedGlassBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.3f)
                            .sound(SoundType.GLASS).noOcclusion(),
                    () -> ChromaBlockEntities.CHROMA_STAINED_GLASS.get()));

    public static final RegistryObject<Block> CHROMA_BANNER = REGISTRY.register("chroma_banner",
            () -> new ChromaBannerBlock(BlockBehaviour.Properties.of()
                    .noCollission().strength(1.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> CHROMA_WALL_BANNER = REGISTRY.register("chroma_wall_banner",
            () -> new ChromaWallBannerBlock(BlockBehaviour.Properties.of()
                    .noCollission().strength(1.0f).sound(SoundType.WOOD)
                    .lootFrom(ChromaBlocks.CHROMA_BANNER)));

    private ChromaBlocks() {}
}
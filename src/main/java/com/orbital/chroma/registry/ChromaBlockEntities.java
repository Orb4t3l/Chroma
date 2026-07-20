package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.blockentity.ChromaBannerBlockEntity;
import com.orbital.chroma.blockentity.ChromaDyeableBlockEntity;
import com.orbital.chroma.blockentity.DyeingTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChromaBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChromaMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<DyeingTableBlockEntity>> DYEING_TABLE = REGISTRY.register(
            "dyeing_table",
            () -> BlockEntityType.Builder.of(DyeingTableBlockEntity::new,
                    ChromaBlocks.DYEING_TABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaDyeableBlockEntity>> CHROMA_WOOL = REGISTRY.register(
            "chroma_wool",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ChromaDyeableBlockEntity(ChromaBlockEntities.CHROMA_WOOL.get(), pos, state),
                    ChromaBlocks.CHROMA_WOOL.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaDyeableBlockEntity>> CHROMA_CARPET = REGISTRY.register(
            "chroma_carpet",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ChromaDyeableBlockEntity(ChromaBlockEntities.CHROMA_CARPET.get(), pos, state),
                    ChromaBlocks.CHROMA_CARPET.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaDyeableBlockEntity>> CHROMA_CONCRETE = REGISTRY.register(
            "chroma_concrete",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ChromaDyeableBlockEntity(ChromaBlockEntities.CHROMA_CONCRETE.get(), pos, state),
                    ChromaBlocks.CHROMA_CONCRETE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaDyeableBlockEntity>> CHROMA_CONCRETE_POWDER = REGISTRY.register(
            "chroma_concrete_powder",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ChromaDyeableBlockEntity(ChromaBlockEntities.CHROMA_CONCRETE_POWDER.get(), pos, state),
                    ChromaBlocks.CHROMA_CONCRETE_POWDER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaDyeableBlockEntity>> CHROMA_TERRACOTTA = REGISTRY.register(
            "chroma_terracotta",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ChromaDyeableBlockEntity(ChromaBlockEntities.CHROMA_TERRACOTTA.get(), pos, state),
                    ChromaBlocks.CHROMA_TERRACOTTA.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaDyeableBlockEntity>> CHROMA_STAINED_GLASS = REGISTRY.register(
            "chroma_stained_glass",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ChromaDyeableBlockEntity(ChromaBlockEntities.CHROMA_STAINED_GLASS.get(), pos, state),
                    ChromaBlocks.CHROMA_STAINED_GLASS.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaDyeableBlockEntity>> CHROMA_STAINED_GLASS_PANE = REGISTRY.register(
            "chroma_stained_glass_pane",
            () -> BlockEntityType.Builder.of(
                    (pos, state) -> new ChromaDyeableBlockEntity(ChromaBlockEntities.CHROMA_STAINED_GLASS_PANE.get(), pos, state),
                    ChromaBlocks.CHROMA_STAINED_GLASS_PANE.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChromaBannerBlockEntity>> CHROMA_BANNER = REGISTRY.register(
            "chroma_banner",
            () -> BlockEntityType.Builder.of(
                    ChromaBannerBlockEntity::new,
                    ChromaBlocks.CHROMA_BANNER.get(),
                    ChromaBlocks.CHROMA_WALL_BANNER.get()).build(null));

    private ChromaBlockEntities() {}
}
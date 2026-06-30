package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.blockentity.ChromaWoolBlockEntity;
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

    public static final RegistryObject<BlockEntityType<ChromaWoolBlockEntity>> CHROMA_WOOL = REGISTRY.register(
            "chroma_wool",
            () -> BlockEntityType.Builder.of(ChromaWoolBlockEntity::new,
                    ChromaBlocks.CHROMA_WOOL.get()).build(null));

    private ChromaBlockEntities() {
    }
}
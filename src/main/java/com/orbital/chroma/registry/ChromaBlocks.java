package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.block.ChromaWoolBlock;
import com.orbital.chroma.block.DyeingTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChromaBlocks {

    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, ChromaMod.MOD_ID);

    public static final RegistryObject<Block> DYEING_TABLE = REGISTRY.register("dyeing_table",
            () -> new DyeingTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.5f)
                    .noOcclusion()));

    public static final RegistryObject<Block> CHROMA_WOOL = REGISTRY.register("chroma_wool",
            () -> new ChromaWoolBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOL)
                    .strength(0.8f)
                    .sound(SoundType.WOOL)
                    .noOcclusion()));

    private ChromaBlocks() {
    }
}
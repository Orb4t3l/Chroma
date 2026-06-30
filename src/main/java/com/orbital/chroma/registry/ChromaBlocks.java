package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.block.DyeingTableBlock;
import net.minecraft.world.level.block.Block;
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

    private ChromaBlocks() {
    }
}
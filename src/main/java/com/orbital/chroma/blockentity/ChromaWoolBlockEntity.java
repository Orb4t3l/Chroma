package com.orbital.chroma.blockentity;

import com.orbital.chroma.api.DyeableBlockEntity;
import com.orbital.chroma.registry.ChromaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChromaWoolBlockEntity extends DyeableBlockEntity {

    public ChromaWoolBlockEntity(BlockPos pos, BlockState state) {
        super(ChromaBlockEntities.CHROMA_WOOL.get(), pos, state, 0xFFFFFF);
    }
}
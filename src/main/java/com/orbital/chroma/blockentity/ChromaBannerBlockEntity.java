package com.orbital.chroma.blockentity;

import com.orbital.chroma.api.DyeableBlockEntity;
import com.orbital.chroma.registry.ChromaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ChromaBannerBlockEntity extends DyeableBlockEntity {

    public ChromaBannerBlockEntity(BlockPos pos, BlockState state) {
        super(ChromaBlockEntities.CHROMA_BANNER.get(), pos, state, 0xFFFFFF);
    }
}
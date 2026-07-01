package com.orbital.chroma.block;

import com.orbital.chroma.blockentity.ChromaBannerBlockEntity;
import com.orbital.chroma.registry.ChromaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;

import javax.annotation.Nullable;

public class ChromaBannerBlock extends BannerBlock {

    public ChromaBannerBlock(Properties properties) {
        super(DyeColor.WHITE, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChromaBannerBlockEntity(pos, state);
    }
}
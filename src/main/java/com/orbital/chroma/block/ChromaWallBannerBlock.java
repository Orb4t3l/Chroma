package com.orbital.chroma.block;

import com.orbital.chroma.blockentity.ChromaBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ChromaWallBannerBlock extends WallBannerBlock {

    public ChromaWallBannerBlock(Properties properties) {
        super(DyeColor.WHITE, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChromaBannerBlockEntity(pos, state);
    }
}
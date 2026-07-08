package com.orbital.chroma.block;

import com.orbital.chroma.api.ColorAPI;
import com.orbital.chroma.blockentity.ChromaBannerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ChromaBannerBlockEntity banner) {
            banner.setChromaColor(ColorAPI.getItemColor(stack, 0xFFFFFF));
        }
    }
}
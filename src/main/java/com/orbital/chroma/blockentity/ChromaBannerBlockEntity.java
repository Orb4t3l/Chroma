package com.orbital.chroma.blockentity;

import com.orbital.chroma.registry.ChromaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChromaBannerBlockEntity extends BannerBlockEntity {

    private int chromaColor = 0xFFFFFF;

    public ChromaBannerBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state, DyeColor.WHITE);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ChromaBlockEntities.CHROMA_BANNER.get();
    }

    public int getChromaColor() {
        return chromaColor;
    }

    public void setChromaColor(int rgb) {
        this.chromaColor = rgb;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ChromaColor", chromaColor);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("ChromaColor")) {
            chromaColor = tag.getInt("ChromaColor");
        } else if (tag.contains("Color")) {
            chromaColor = tag.getInt("Color");
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("ChromaColor", chromaColor);
        return tag;
    }
}
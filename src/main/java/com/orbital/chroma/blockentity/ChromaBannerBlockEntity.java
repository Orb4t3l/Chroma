package com.orbital.chroma.blockentity;

import com.orbital.chroma.registry.ChromaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChromaBannerBlockEntity extends BannerBlockEntity {

    private int chromaColor = 0xFFFFFF;

    public ChromaBannerBlockEntity(BlockPos pos, BlockState state) {
        super(ChromaBlockEntities.CHROMA_BANNER.get(), pos, state, net.minecraft.world.item.DyeColor.WHITE);
    }

    public ChromaBannerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, net.minecraft.world.item.DyeColor.WHITE);
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
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("ChromaColor", chromaColor);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
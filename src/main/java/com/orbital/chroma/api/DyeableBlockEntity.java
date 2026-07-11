package com.orbital.chroma.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DyeableBlockEntity extends BlockEntity implements IDyeable {

    private int color;
    private int gradientEnd = -1;

    public DyeableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int defaultColor) {
        super(type, pos, state);
        this.color = defaultColor;
    }

    @Override
    public int getColor() { return color; }

    @Override
    public void setColor(int rgb) {
        this.color = rgb;
        setChanged();
        syncToClient();
    }

    @Override
    public int getGradientEndColor() { return gradientEnd; }

    @Override
    public void setGradientEndColor(int rgb) {
        this.gradientEnd = rgb;
        setChanged();
        syncToClient();
    }

    public void clearGradient() {
        this.gradientEnd = -1;
        setChanged();
        syncToClient();
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Color", color);
        tag.putInt("GradientEnd", gradientEnd);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Color")) {
            color = tag.getInt("Color");
        }
        gradientEnd = tag.contains("GradientEnd") ? tag.getInt("GradientEnd") : -1;
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Color", color);
        tag.putInt("GradientEnd", gradientEnd);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void applyColorFromStack(BlockEntity be, ItemStack stack) {
        if (!(be instanceof IDyeable dyeable)) return;
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();

        int color = -1;
        int gradientEnd = -1;

        if (tag.contains("BlockEntityTag")) {
            CompoundTag bet = tag.getCompound("BlockEntityTag");
            if (bet.contains("Color")) color = bet.getInt("Color");
            if (bet.contains("GradientEnd")) gradientEnd = bet.getInt("GradientEnd");
        }
        if (color == -1 && tag.contains("ChromaColor")) {
            color = tag.getInt("ChromaColor");
        }
        if (gradientEnd == -1 && tag.contains("ChromaGradientEnd")) {
            gradientEnd = tag.getInt("ChromaGradientEnd");
        }

        if (color != -1) dyeable.setColor(color);
        dyeable.setGradientEndColor(gradientEnd);
    }
}
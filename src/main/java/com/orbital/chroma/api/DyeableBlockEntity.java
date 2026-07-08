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
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Color", color);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Color")) {
            color = tag.getInt("Color");
            if (level != null && level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                        Block.UPDATE_IMMEDIATE);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Color", color);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void applyColorFromStack(BlockEntity be, net.minecraft.world.item.ItemStack stack) {
        if (!(be instanceof IDyeable dyeable)) return;
        if (!stack.hasTag()) return;
        CompoundTag tag = stack.getTag();
        if (tag.contains("BlockEntityTag")) {
            CompoundTag bet = tag.getCompound("BlockEntityTag");
            if (bet.contains("Color")) {
                dyeable.setColor(bet.getInt("Color"));
                return;
            }
        }
        if (tag.contains("ChromaColor")) {
            dyeable.setColor(tag.getInt("ChromaColor"));
        }
    }

}
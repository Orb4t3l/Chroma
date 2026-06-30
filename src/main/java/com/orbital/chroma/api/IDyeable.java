package com.orbital.chroma.api;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IDyeable {

    int getColor();

    void setColor(int rgb);

    default int getDefaultColor() {
        return 0xFFFFFF;
    }

    default boolean canBeDyed() {
        return true;
    }

    static IDyeable of(BlockEntity be) {
        if (be instanceof IDyeable dyeable) {
            return dyeable;
        }
        return null;
    }
}
package com.orbital.chroma.api;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IDyeable {

    int getColor();

    void setColor(int rgb);

    default int getGradientEndColor() {
        return -1;
    }

    default void setGradientEndColor(int rgb) {
    }

    default boolean hasGradient() {
        return getGradientEndColor() != -1;
    }

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
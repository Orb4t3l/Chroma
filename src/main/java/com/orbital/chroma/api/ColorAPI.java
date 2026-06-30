package com.orbital.chroma.api;


import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashSet;
import java.util.Set;

public final class ColorAPI {

    private static final Set<Block> REGISTERED_DYEABLE_BLOCKS = new HashSet<>();

    private ColorAPI() {
    }

    public static void registerDyeable(Block block) {
        REGISTERED_DYEABLE_BLOCKS.add(block);
    }

    public static boolean isDyeable(Block block) {
        return REGISTERED_DYEABLE_BLOCKS.contains(block);
    }

    public static void registerBlockTint(BlockColors blockColors, Block block) {
        blockColors.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IDyeable dyeable) {
                    return dyeable.getColor();
                }
            }
            return 0xFFFFFF;
        }, block);
    }

    public static void registerItemTint(ItemColors itemColors, Block block, int defaultColor) {
        itemColors.register((stack, tintIndex) -> getItemColor(stack, defaultColor), block.asItem());
    }

    public static int getItemColor(ItemStack stack, int defaultColor) {
        if (stack.hasTag() && stack.getTag().contains("ChromaColor")) {
            return stack.getTag().getInt("ChromaColor");
        }
        return defaultColor;
    }

    public static void setItemColor(ItemStack stack, int rgb) {
        stack.getOrCreateTag().putInt("ChromaColor", rgb);
    }
}
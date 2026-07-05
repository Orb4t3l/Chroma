package com.orbital.chroma.api;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ColorAPI {

    private static final Set<Block> REGISTERED_DYEABLE_BLOCKS = new HashSet<>();
    private static final Set<Item> DISPLAY_COLOR_ITEMS = new HashSet<>();
    private static final Map<Item, BiConsumer<ItemStack, Integer>> CUSTOM_COLOR_SETTERS = new HashMap<>();
    private static final Map<Item, Function<ItemStack, Integer>> CUSTOM_COLOR_GETTERS = new HashMap<>();

    private ColorAPI() {}

    public static void registerDyeable(Block block) {
        REGISTERED_DYEABLE_BLOCKS.add(block);
    }

    public static boolean isDyeable(Block block) {
        return REGISTERED_DYEABLE_BLOCKS.contains(block);
    }

    public static void registerDyeableItem(Item item) {
        DISPLAY_COLOR_ITEMS.add(item);
    }

    public static void registerCustomDyeableItem(Item item,
                                                 BiConsumer<ItemStack, Integer> setter,
                                                 Function<ItemStack, Integer> getter) {
        CUSTOM_COLOR_SETTERS.put(item, setter);
        CUSTOM_COLOR_GETTERS.put(item, getter);
    }

    public static boolean isDyeableItem(Item item) {
        return DISPLAY_COLOR_ITEMS.contains(item)
                || CUSTOM_COLOR_SETTERS.containsKey(item);
    }

    public static void applyColorToItem(ItemStack stack, int rgb) {
        Item item = stack.getItem();
        if (CUSTOM_COLOR_SETTERS.containsKey(item)) {
            CUSTOM_COLOR_SETTERS.get(item).accept(stack, rgb);
        } else {
            stack.getOrCreateTagElement("display").putInt("color", rgb);
        }
    }

    public static int getItemColor(ItemStack stack, int defaultColor) {
        Item item = stack.getItem();
        if (CUSTOM_COLOR_GETTERS.containsKey(item)) {
            return CUSTOM_COLOR_GETTERS.get(item).apply(stack);
        }
        if (stack.hasTag()) {
            if (stack.getTag().contains("ChromaColor")) {
                return stack.getTag().getInt("ChromaColor");
            }
            if (stack.getTag().contains("display")) {
                var display = stack.getTag().getCompound("display");
                if (display.contains("color")) {
                    return display.getInt("color");
                }
            }
        }
        return defaultColor;
    }

    public static void setItemColor(ItemStack stack, int rgb) {
        stack.getOrCreateTag().putInt("ChromaColor", rgb);
    }

    public static void registerBlockTint(BlockColors blockColors, Block block) {
        blockColors.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IDyeable dyeable) return dyeable.getColor();
            }
            return 0xFFFFFF;
        }, block);
    }

    public static void registerItemTint(ItemColors itemColors, Block block, int defaultColor) {
        itemColors.register((stack, tintIndex) -> getItemColor(stack, defaultColor), block.asItem());
    }
}
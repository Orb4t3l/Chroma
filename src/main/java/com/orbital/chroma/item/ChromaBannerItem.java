package com.orbital.chroma.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;

public class ChromaBannerItem extends StandingAndWallBlockItem {

    public ChromaBannerItem(Block floorBlock, Block wallBlock, Properties properties) {
        super(floorBlock, wallBlock, properties, Direction.DOWN);
    }
}
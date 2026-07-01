package com.orbital.chroma.blockentity;

import com.orbital.chroma.api.DyeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChromaDyeableBlockEntity extends DyeableBlockEntity {

    public ChromaDyeableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 0xFFFFFF);
    }
}
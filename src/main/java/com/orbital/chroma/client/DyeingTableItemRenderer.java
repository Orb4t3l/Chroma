package com.orbital.chroma.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.orbital.chroma.block.DyeingTableBlock;
import com.orbital.chroma.blockentity.DyeingTableBlockEntity;
import com.orbital.chroma.registry.ChromaBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DyeingTableItemRenderer extends BlockEntityWithoutLevelRenderer {

    private DyeingTableBlockEntity dummyBE;

    public DyeingTableItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    private DyeingTableBlockEntity getDummyBE() {
        if (dummyBE == null) {
            BlockState state = ChromaBlocks.DYEING_TABLE.get().defaultBlockState()
                    .setValue(DyeingTableBlock.FACING, Direction.SOUTH)
                    .setValue(DyeingTableBlock.PART, DyeingTableBlock.Part.CONTROLLER);
            dummyBE = new DyeingTableBlockEntity(BlockPos.ZERO, state);
        }
        if (dummyBE.getLevel() == null && Minecraft.getInstance().level != null) {
            dummyBE.setLevel(Minecraft.getInstance().level);
        }
        return dummyBE;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx,
                             PoseStack poseStack, MultiBufferSource bufferSource,
                             int packedLight, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.0);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(getDummyBE(), poseStack, bufferSource, packedLight, overlay);

        poseStack.popPose();
    }
}
package com.orbital.chroma.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.orbital.chroma.api.ColorAPI;
import com.orbital.chroma.blockentity.ChromaBannerBlockEntity;
import com.orbital.chroma.registry.ChromaBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ChromaBannerItemRenderer extends BlockEntityWithoutLevelRenderer {

    private ChromaBannerBlockEntity dummyBE;

    public ChromaBannerItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    private ChromaBannerBlockEntity getDummyBE() {
        if (dummyBE == null) {
            BlockState state = ChromaBlocks.CHROMA_BANNER.get().defaultBlockState();
            dummyBE = new ChromaBannerBlockEntity(BlockPos.ZERO, state);
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
        ChromaBannerBlockEntity be = getDummyBE();

        int color = ColorAPI.getItemColor(stack, 0xFFFFFF);
        be.setChromaColor(color);

        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            var betTag = stack.getTag().getCompound("BlockEntityTag");
            if (betTag.contains("Patterns")) {
                var fullTag = be.saveWithoutMetadata();
                fullTag.put("Patterns", betTag.getList("Patterns", 10));
                be.load(fullTag);
                be.setChromaColor(color);
            }
        }

        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(be, poseStack, bufferSource, packedLight, overlay);
    }
}
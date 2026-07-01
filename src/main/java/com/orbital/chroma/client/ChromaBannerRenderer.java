package com.orbital.chroma.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.orbital.chroma.blockentity.ChromaBannerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public class ChromaBannerRenderer extends BannerRenderer {

    public ChromaBannerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BannerBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int overlay) {
        if (!(be instanceof ChromaBannerBlockEntity chromaBe)) {
            super.render(be, partialTick, poseStack, bufferSource, packedLight, overlay);
            return;
        }

        int rgb = chromaBe.getChromaColor();

        ChromaColorBufferSource chromaBuffer = new ChromaColorBufferSource(bufferSource, rgb);
        super.render(be, partialTick, poseStack, chromaBuffer, packedLight, overlay);
    }
}
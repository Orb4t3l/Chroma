package com.orbital.chroma.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.orbital.chroma.blockentity.ChromaBannerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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
        float r = ((rgb >> 16) & 0xFF) / 255.0f;
        float g = ((rgb >> 8)  & 0xFF) / 255.0f;
        float b = ( rgb        & 0xFF) / 255.0f;

        RenderSystem.setShaderColor(r, g, b, 1.0f);
        super.render(be, partialTick, poseStack, bufferSource, packedLight, overlay);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
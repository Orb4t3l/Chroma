package com.orbital.chroma.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.orbital.chroma.block.DyeingTableBlock;
import com.orbital.chroma.blockentity.DyeingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class DyeingTableRenderer implements BlockEntityRenderer<DyeingTableBlockEntity> {

    public DyeingTableRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(DyeingTableBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int overlay) {
        BlockState state = be.getBlockState();
        if (state.getValue(DyeingTableBlock.PART) != DyeingTableBlock.Part.CONTROLLER) return;

        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getBlockRenderer().getBlockModel(state);

        mc.getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                bufferSource.getBuffer(RenderType.cutoutMipped()),
                state, model,
                1.0f, 1.0f, 1.0f,
                packedLight, overlay,
                ModelData.EMPTY, RenderType.cutoutMipped());
    }

    @Override
    public boolean shouldRenderOffScreen(DyeingTableBlockEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() { return 64; }
}
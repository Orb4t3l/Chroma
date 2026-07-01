package com.orbital.chroma.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class ChromaColorBufferSource implements MultiBufferSource {

    private final MultiBufferSource delegate;
    private final int rgb;
    private boolean firstLayer = true;

    public ChromaColorBufferSource(MultiBufferSource delegate, int rgb) {
        this.delegate = delegate;
        this.rgb = rgb;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        VertexConsumer inner = delegate.getBuffer(renderType);
        if (firstLayer) {
            firstLayer = false;
            return new TintedVertexConsumer(inner, rgb);
        }
        return inner;
    }

    private static final class TintedVertexConsumer implements VertexConsumer {

        private final VertexConsumer delegate;
        private final float r;
        private final float g;
        private final float b;

        TintedVertexConsumer(VertexConsumer delegate, int rgb) {
            this.delegate = delegate;
            this.r = ((rgb >> 16) & 0xFF) / 255.0f;
            this.g = ((rgb >> 8)  & 0xFF) / 255.0f;
            this.b = ( rgb        & 0xFF) / 255.0f;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            delegate.color(
                    (int)(red   * r),
                    (int)(green * g),
                    (int)(blue  * b),
                    alpha
            );
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) { delegate.uv(u, v); return this; }

        @Override
        public VertexConsumer overlayCoords(int u, int v) { delegate.overlayCoords(u, v); return this; }

        @Override
        public VertexConsumer uv2(int u, int v) { delegate.uv2(u, v); return this; }

        @Override
        public VertexConsumer normal(float x, float y, float z) { delegate.normal(x, y, z); return this; }

        @Override
        public void endVertex() { delegate.endVertex(); }

        @Override
        public void defaultColor(int r, int g, int b, int a) { delegate.defaultColor(r, g, b, a); }

        @Override
        public void unsetDefaultColor() { delegate.unsetDefaultColor(); }
    }
}
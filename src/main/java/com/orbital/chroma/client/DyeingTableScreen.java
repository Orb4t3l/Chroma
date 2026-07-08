package com.orbital.chroma.client;

import com.orbital.chroma.menu.DyeingTableMenu;
import com.orbital.chroma.network.ApplyGradientPacket;
import com.orbital.chroma.network.ChromaNetwork;
import com.orbital.chroma.network.SetDyeColorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.IntConsumer;

public class DyeingTableScreen extends AbstractContainerScreen<DyeingTableMenu> {

    @SuppressWarnings("deprecation")
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("chroma", "textures/gui/dyeing_table.png");

    private static final int SLIDER_X   = 12;
    private static final int SLIDER_W   = 152;
    private static final int SLIDER_H   = 14;
    private static final int SLIDER_R_Y = 40;
    private static final int SLIDER_G_Y = 58;
    private static final int SLIDER_B_Y = 76;

    private static final int MODE_Y    = 23;
    private static final int MODE_W    = 72;
    private static final int MODE_H    = 12;

    private static final int SWATCH_Y    = 23;
    private static final int SWATCH_SIZE = 12;
    private static final int SWATCH_A_X  = 92;
    private static final int SWATCH_B_X  = 110;

    private static final int STRIP_X = 12;
    private static final int STRIP_Y = 95;
    private static final int STRIP_W = 152;
    private static final int STRIP_H = 12;

    private static final int PREVIEW_X = 12;
    private static final int PREVIEW_Y = 95;
    private static final int PREVIEW_W = 74;
    private static final int PREVIEW_H = 12;

    private static final int HEX_X = 98;
    private static final int HEX_Y = 95;
    private static final int HEX_W = 66;
    private static final int HEX_H = 12;

    private static final int LABEL_OFFSET_X = 10;

    private GradientSlider redSlider;
    private GradientSlider greenSlider;
    private GradientSlider blueSlider;
    private EditBox hexBox;
    private Button modeBtn;

    private boolean gradientMode = false;
    private boolean editingA     = true;

    private int colorA;
    private int colorB = 0x0000FF;

    private int red, green, blue;
    private int lastKnownColor  = -1;
    private boolean updatingHex    = false;
    private boolean updatingSlider = false;

    public DyeingTableScreen(DyeingTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth      = 176;
        this.imageHeight     = 204;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int startColor = menu.getClientColor();
        colorA = startColor;
        setRGB(startColor);
        lastKnownColor = startColor;

        redSlider = new GradientSlider(leftPos + SLIDER_X, topPos + SLIDER_R_Y,
                SLIDER_W, SLIDER_H, red, Channel.RED,
                v -> { if (!updatingHex) { red = v; onSliderChanged(); } });

        greenSlider = new GradientSlider(leftPos + SLIDER_X, topPos + SLIDER_G_Y,
                SLIDER_W, SLIDER_H, green, Channel.GREEN,
                v -> { if (!updatingHex) { green = v; onSliderChanged(); } });

        blueSlider = new GradientSlider(leftPos + SLIDER_X, topPos + SLIDER_B_Y,
                SLIDER_W, SLIDER_H, blue, Channel.BLUE,
                v -> { if (!updatingHex) { blue = v; onSliderChanged(); } });

        addRenderableWidget(redSlider);
        addRenderableWidget(greenSlider);
        addRenderableWidget(blueSlider);

        hexBox = new EditBox(font, leftPos + HEX_X + 8, topPos + HEX_Y,
                HEX_W - 8, HEX_H, Component.empty());
        hexBox.setMaxLength(6);
        hexBox.setBordered(true);
        hexBox.setResponder(this::onHexChanged);
        hexBox.setValue(String.format("%06X", startColor));
        addRenderableWidget(hexBox);

        modeBtn = Button.builder(Component.literal("Gradient ▶"), btn -> toggleMode())
                .pos(leftPos + 8, topPos + MODE_Y)
                .size(MODE_W, MODE_H)
                .build();
        addRenderableWidget(modeBtn);
    }

    private void toggleMode() {
        gradientMode = !gradientMode;
        hexBox.visible = !gradientMode;
        if (gradientMode) {
            modeBtn.setMessage(Component.literal("◀ Single"));
            editingA = true;
            pushToSliders(colorA);
        } else {
            modeBtn.setMessage(Component.literal("Gradient ▶"));
            pushToSliders(menu.getClientColor());
        }
    }

    private void setRGB(int color) {
        red   = (color >> 16) & 0xFF;
        green = (color >> 8)  & 0xFF;
        blue  =  color        & 0xFF;
    }

    private void pushToSliders(int color) {
        setRGB(color);
        updatingSlider = true;
        redSlider.setChannel(red);
        greenSlider.setChannel(green);
        blueSlider.setChannel(blue);
        hexBox.setValue(String.format("%06X", color));
        updatingSlider = false;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (gradientMode) return;
        int serverColor = menu.getClientColor();
        if (serverColor != lastKnownColor) {
            lastKnownColor = serverColor;
            colorA = serverColor;
            pushToSliders(serverColor);
        }
    }

    private GradientSlider draggingSlider = null;

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (gradientMode) {
            int ax = leftPos + SWATCH_A_X, bx = leftPos + SWATCH_B_X;
            int sy = topPos + SWATCH_Y;
            if (mx >= ax && mx <= ax + SWATCH_SIZE && my >= sy && my <= sy + SWATCH_SIZE) {
                if (!editingA) { editingA = true; pushToSliders(colorA); }
                return true;
            }
            if (mx >= bx && mx <= bx + SWATCH_SIZE && my >= sy && my <= sy + SWATCH_SIZE) {
                if (editingA) { editingA = false; pushToSliders(colorB); }
                return true;
            }
            int sx1 = leftPos + STRIP_X, sx2 = sx1 + STRIP_W;
            int sy1 = topPos  + STRIP_Y, sy2 = sy1 + STRIP_H;
            if (mx >= sx1 && mx <= sx2 && my >= sy1 && my <= sy2) {
                ChromaNetwork.CHANNEL.sendToServer(new ApplyGradientPacket(colorA, colorB));
                gradientMode = false;
                hexBox.visible = true;
                modeBtn.setMessage(Component.literal("Gradient ▶"));
                return true;
            }
        }
        if (redSlider.isMouseOver(mx, my)) {
            draggingSlider = redSlider;
            return redSlider.mouseClicked(mx, my, btn);
        }
        if (greenSlider.isMouseOver(mx, my)) {
            draggingSlider = greenSlider;
            return greenSlider.mouseClicked(mx, my, btn);
        }
        if (blueSlider.isMouseOver(mx, my)) {
            draggingSlider = blueSlider;
            return blueSlider.mouseClicked(mx, my, btn);
        }
        draggingSlider = null;
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (draggingSlider != null) return draggingSlider.mouseDragged(mx, my, btn, dx, dy);
        return super.mouseDragged(mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (draggingSlider != null) {
            boolean result = draggingSlider.mouseReleased(mx, my, btn);
            draggingSlider = null;
            return result;
        }
        return super.mouseReleased(mx, my, btn);
    }

    private void onSliderChanged() {
        int color = currentColor();
        if (gradientMode) { if (editingA) colorA = color; else colorB = color; }
        updatingSlider = true;
        hexBox.setValue(String.format("%06X", color));
        updatingSlider = false;
        if (!gradientMode || editingA) ChromaNetwork.CHANNEL.sendToServer(new SetDyeColorPacket(color));
    }

    private void onHexChanged(String value) {
        if (updatingSlider) return;
        if (value.length() != 6) return;
        try {
            int color = Integer.parseInt(value, 16);
            updatingHex = true;
            red   = (color >> 16) & 0xFF;
            green = (color >> 8)  & 0xFF;
            blue  =  color        & 0xFF;
            redSlider.setChannel(red);
            greenSlider.setChannel(green);
            blueSlider.setChannel(blue);
            updatingHex = false;
            if (gradientMode) { if (editingA) colorA = color; else colorB = color; }
            if (!gradientMode || editingA) ChromaNetwork.CHANNEL.sendToServer(new SetDyeColorPacket(color));
        } catch (NumberFormatException ignored) {}
    }

    private int currentColor() { return (red << 16) | (green << 8) | blue; }

    @Override
    protected void renderBg(GuiGraphics g, float partial, int mx, int my) {
        int x = leftPos, y = topPos;
        g.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if (gradientMode) {
            int ax = x + SWATCH_A_X, bx = x + SWATCH_B_X, sy = y + SWATCH_Y;
            g.fill(ax, sy, ax + SWATCH_SIZE, sy + SWATCH_SIZE, 0xFF000000 | colorA);
            g.renderOutline(ax - 1, sy - 1, SWATCH_SIZE + 2, SWATCH_SIZE + 2,
                    editingA ? 0xFFFFFFFF : 0xFF555555);
            g.fill(bx, sy, bx + SWATCH_SIZE, sy + SWATCH_SIZE, 0xFF000000 | colorB);
            g.renderOutline(bx - 1, sy - 1, SWATCH_SIZE + 2, SWATCH_SIZE + 2,
                    !editingA ? 0xFFFFFFFF : 0xFF555555);

            renderGradientStrip(g, x + STRIP_X, y + STRIP_Y, STRIP_W, STRIP_H, colorA, colorB);
            boolean hovered = mx >= x + STRIP_X && mx <= x + STRIP_X + STRIP_W
                    && my >= y + STRIP_Y && my <= y + STRIP_Y + STRIP_H;
            g.drawCenteredString(font, "Click to apply",
                    x + STRIP_X + STRIP_W / 2, y + STRIP_Y + (STRIP_H - 8) / 2,
                    hovered ? 0xFFFFFFFF : 0xCCFFFFFF);
        } else {
            int color = currentColor();
            g.fill(x + PREVIEW_X, y + PREVIEW_Y,
                    x + PREVIEW_X + PREVIEW_W, y + PREVIEW_Y + PREVIEW_H,
                    0xFF000000 | color);
            g.renderOutline(x + PREVIEW_X - 1, y + PREVIEW_Y - 1,
                    PREVIEW_W + 2, PREVIEW_H + 2, 0xFF444444);
            g.drawString(font, "#", x + HEX_X - 1, y + HEX_Y + 2, 0x555555, false);
        }
    }

    private void renderGradientStrip(GuiGraphics g, int x, int y, int w, int h, int ca, int cb) {
        int rA = (ca >> 16) & 0xFF, gA = (ca >> 8) & 0xFF, bA = ca & 0xFF;
        int rB = (cb >> 16) & 0xFF, gB = (cb >> 8) & 0xFF, bB = cb & 0xFF;
        for (int i = 0; i < w; i++) {
            float t  = (float) i / (w - 1);
            int r    = Math.round(rA + (rB - rA) * t);
            int gr   = Math.round(gA + (gB - gA) * t);
            int b    = Math.round(bA + (bB - bA) * t);
            g.fill(x + i, y, x + i + 1, y + h, 0xFF000000 | (r << 16) | (gr << 8) | b);
        }
        g.renderOutline(x - 1, y - 1, w + 2, h + 2, 0xFF444444);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(font, title, 8, 6, 0x404040, false);
        g.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false);
        g.drawString(font, "R", SLIDER_X - LABEL_OFFSET_X - 1, SLIDER_R_Y + 3, 0xBB2222, false);
        g.drawString(font, "G", SLIDER_X - LABEL_OFFSET_X - 1, SLIDER_G_Y + 3, 0x22AA22, false);
        g.drawString(font, "B", SLIDER_X - LABEL_OFFSET_X - 1, SLIDER_B_Y + 3, 0x2222BB, false);
        if (gradientMode) {
            g.drawString(font, editingA ? "▶A" : "▶B",
                    SWATCH_B_X + SWATCH_SIZE + 3, SWATCH_Y + 2, 0xAAAAAA, false);
        }
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partial) {
        renderBackground(g);
        super.render(g, mx, my, partial);
        renderTooltip(g, mx, my);
    }

    enum Channel { RED, GREEN, BLUE }

    private static final class GradientSlider extends AbstractSliderButton {

        private final Channel channel;
        private final IntConsumer onChange;

        GradientSlider(int x, int y, int w, int h, int init, Channel channel, IntConsumer onChange) {
            super(x, y, w, h, Component.literal(String.valueOf(init)), init / 255.0);
            this.channel  = channel;
            this.onChange = onChange;
        }

        int getChannelValue() { return (int) Math.round(value * 255); }
        void setChannel(int v) { this.value = v / 255.0; updateMessage(); }

        @Override protected void updateMessage() {
            setMessage(Component.literal(String.valueOf(getChannelValue())));
        }
        @Override protected void applyValue() { onChange.accept(getChannelValue()); }

        @Override
        public void renderWidget(GuiGraphics g, int mx, int my, float partial) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            int dark = switch (channel) {
                case RED -> 0xFF1A0000; case GREEN -> 0xFF001A00; case BLUE -> 0xFF00001A;
            };
            int full = switch (channel) {
                case RED -> 0xFFFF0000; case GREEN -> 0xFF00FF00; case BLUE -> 0xFF0000FF;
            };
            g.fillGradient(x, y, x + w, y + h, dark, full);
            g.renderOutline(x - 1, y - 1, w + 2, h + 2, isHovered ? 0xFFAAAAAA : 0xFF555555);
            int hx = x + (int) (value * (w - 6));
            g.fill(hx, y - 1, hx + 6, y + h + 1, 0xFFFFFFFF);
            g.fill(hx + 1, y, hx + 5, y + h, 0xFF888888);
            Font f = Minecraft.getInstance().font;
            g.drawString(f, String.valueOf(getChannelValue()), x + w + 4, y + (h - 8) / 2, 0xFF404040, false);
        }
    }
}
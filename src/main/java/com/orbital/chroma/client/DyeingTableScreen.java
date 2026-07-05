package com.orbital.chroma.client;

import com.orbital.chroma.menu.DyeingTableMenu;
import com.orbital.chroma.network.ChromaNetwork;
import com.orbital.chroma.network.SetDyeColorPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.IntConsumer;

public class DyeingTableScreen extends AbstractContainerScreen<DyeingTableMenu> {

    @SuppressWarnings("deprecation")
    private static final ResourceLocation TEXTURE = new ResourceLocation("chroma", "textures/gui/dyeing_table.png");

    private static final int SLIDER_X = 12;
    private static final int SLIDER_W = 152;
    private static final int SLIDER_H = 14;
    private static final int SLIDER_R_Y = 48;
    private static final int SLIDER_G_Y = 66;
    private static final int SLIDER_B_Y = 84;

    private static final int PREVIEW_X = 12;
    private static final int PREVIEW_Y = 106;
    private static final int PREVIEW_W = 76;
    private static final int PREVIEW_H = 16;

    private static final int HEX_X = 104;
    private static final int HEX_Y = 106;
    private static final int HEX_W = 60;
    private static final int HEX_H = 16;

    private GradientSlider redSlider;
    private GradientSlider greenSlider;
    private GradientSlider blueSlider;
    private EditBox hexBox;

    private int red;
    private int green;
    private int blue;
    private int lastKnownColor = -1;
    private boolean updatingFromHex = false;
    private boolean updatingFromSlider = false;

    public DyeingTableScreen(DyeingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 204;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int startColor = menu.getClientColor();
        red   = (startColor >> 16) & 0xFF;
        green = (startColor >> 8)  & 0xFF;
        blue  =  startColor        & 0xFF;
        lastKnownColor = startColor;

        redSlider = new GradientSlider(leftPos + SLIDER_X, topPos + SLIDER_R_Y, SLIDER_W, SLIDER_H,
                red, Channel.RED,
                v -> { if (!updatingFromHex) { red = v; onSliderChanged(); } });

        greenSlider = new GradientSlider(leftPos + SLIDER_X, topPos + SLIDER_G_Y, SLIDER_W, SLIDER_H,
                green, Channel.GREEN,
                v -> { if (!updatingFromHex) { green = v; onSliderChanged(); } });

        blueSlider = new GradientSlider(leftPos + SLIDER_X, topPos + SLIDER_B_Y, SLIDER_W, SLIDER_H,
                blue, Channel.BLUE,
                v -> { if (!updatingFromHex) { blue = v; onSliderChanged(); } });

        addRenderableWidget(redSlider);
        addRenderableWidget(greenSlider);
        addRenderableWidget(blueSlider);

        hexBox = new EditBox(font, leftPos + HEX_X + 9, topPos + HEX_Y, HEX_W - 9, HEX_H, Component.empty());
        hexBox.setMaxLength(6);
        hexBox.setBordered(true);
        hexBox.setResponder(this::onHexChanged);
        hexBox.setValue(String.format("%06X", startColor));
        addRenderableWidget(hexBox);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        int serverColor = menu.getClientColor();
        if (serverColor != lastKnownColor) {
            lastKnownColor = serverColor;
            red   = (serverColor >> 16) & 0xFF;
            green = (serverColor >> 8)  & 0xFF;
            blue  =  serverColor        & 0xFF;
            updatingFromSlider = true;
            redSlider.setChannel(red);
            greenSlider.setChannel(green);
            blueSlider.setChannel(blue);
            hexBox.setValue(String.format("%06X", serverColor));
            updatingFromSlider = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (redSlider.isMouseOver(mouseX, mouseY)) {
            setFocused(redSlider);
            return redSlider.mouseClicked(mouseX, mouseY, button);
        }
        if (greenSlider.isMouseOver(mouseX, mouseY)) {
            setFocused(greenSlider);
            return greenSlider.mouseClicked(mouseX, mouseY, button);
        }
        if (blueSlider.isMouseOver(mouseX, mouseY)) {
            setFocused(blueSlider);
            return blueSlider.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (getFocused() instanceof GradientSlider slider) {
            return slider.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (getFocused() instanceof GradientSlider slider) {
            return slider.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void onSliderChanged() {
        updatingFromSlider = true;
        hexBox.setValue(String.format("%06X", currentColor()));
        updatingFromSlider = false;
        sendColor();
    }

    private void onHexChanged(String value) {
        if (updatingFromSlider) return;
        if (value.length() != 6) return;
        try {
            int color = Integer.parseInt(value, 16);
            updatingFromHex = true;
            red   = (color >> 16) & 0xFF;
            green = (color >> 8)  & 0xFF;
            blue  =  color        & 0xFF;
            redSlider.setChannel(red);
            greenSlider.setChannel(green);
            blueSlider.setChannel(blue);
            updatingFromHex = false;
            sendColor();
        } catch (NumberFormatException ignored) {}
    }

    private int currentColor() {
        return (red << 16) | (green << 8) | blue;
    }

    private void sendColor() {
        ChromaNetwork.CHANNEL.sendToServer(new SetDyeColorPacket(currentColor()));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        int color = currentColor();
        graphics.fill(x + PREVIEW_X, y + PREVIEW_Y,
                x + PREVIEW_X + PREVIEW_W, y + PREVIEW_Y + PREVIEW_H, 0xFF000000 | color);
        graphics.renderOutline(x + PREVIEW_X - 1, y + PREVIEW_Y - 1, PREVIEW_W + 2, PREVIEW_H + 2, 0xFF444444);
        graphics.drawString(font, "#", x + HEX_X - 1, y + HEX_Y + 4, 0x555555, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0x404040, false);
        graphics.drawString(font, "R", SLIDER_X - 1, SLIDER_R_Y + 3, 0xBB2222, false);
        graphics.drawString(font, "G", SLIDER_X - 1, SLIDER_G_Y + 3, 0x22AA22, false);
        graphics.drawString(font, "B", SLIDER_X - 1, SLIDER_B_Y + 3, 0x2222BB, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    enum Channel { RED, GREEN, BLUE }

    private static final class GradientSlider extends AbstractSliderButton {

        private final Channel channel;
        private final IntConsumer onChange;

        GradientSlider(int x, int y, int width, int height, int initialValue, Channel channel, IntConsumer onChange) {
            super(x, y, width, height, Component.literal(String.valueOf(initialValue)), initialValue / 255.0);
            this.channel = channel;
            this.onChange = onChange;
        }

        int getChannelValue() {
            return (int) Math.round(value * 255);
        }

        void setChannel(int v) {
            this.value = v / 255.0;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(String.valueOf(getChannelValue())));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getChannelValue());
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            int w = getWidth();
            int h = getHeight();

            int darkColor = switch (channel) {
                case RED   -> 0xFF1A0000;
                case GREEN -> 0xFF001A00;
                case BLUE  -> 0xFF00001A;
            };
            int fullColor = switch (channel) {
                case RED   -> 0xFFFF0000;
                case GREEN -> 0xFF00FF00;
                case BLUE  -> 0xFF0000FF;
            };

            graphics.fillGradient(x, y, x + w, y + h, darkColor, fullColor);
            graphics.renderOutline(x - 1, y - 1, w + 2, h + 2, isHovered ? 0xFFAAAAAA : 0xFF555555);

            int handleX = x + (int) (value * (w - 6));
            graphics.fill(handleX, y - 1, handleX + 6, y + h + 1, 0xFFFFFFFF);
            graphics.fill(handleX + 1, y, handleX + 5, y + h, 0xFF888888);

            Font font = Minecraft.getInstance().font;
            graphics.drawString(font, String.valueOf(getChannelValue()), x + w + 4, y + (h - 8) / 2, 0xFF404040, false);
        }
    }
}
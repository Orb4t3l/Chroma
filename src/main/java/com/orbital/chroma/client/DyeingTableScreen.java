package com.orbital.chroma.client;

import com.orbital.chroma.menu.DyeingTableMenu;
import com.orbital.chroma.network.ChromaNetwork;
import com.orbital.chroma.network.SetDyeColorPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DyeingTableScreen extends AbstractContainerScreen<DyeingTableMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("chroma", "textures/gui/dyeing_table.png");

    private ColorSlider redSlider;
    private ColorSlider greenSlider;
    private ColorSlider blueSlider;
    private EditBox hexBox;

    private int red;
    private int green;
    private int blue;

    public DyeingTableScreen(DyeingTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 196;
        this.imageHeight = 202;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        int startColor = menu.getClientColor();
        red = (startColor >> 16) & 0xFF;
        green = (startColor >> 8) & 0xFF;
        blue = startColor & 0xFF;

        redSlider = new ColorSlider(x + 100, y + 20, "R", red, value -> {
            red = value;
            onColorChanged();
        });
        greenSlider = new ColorSlider(x + 100, y + 38, "G", green, value -> {
            green = value;
            onColorChanged();
        });
        blueSlider = new ColorSlider(x + 100, y + 56, "B", blue, value -> {
            blue = value;
            onColorChanged();
        });

        addRenderableWidget(redSlider);
        addRenderableWidget(greenSlider);
        addRenderableWidget(blueSlider);

        hexBox = new EditBox(font, x + 100, y + 78, 70, 16, Component.literal("hex"));
        hexBox.setMaxLength(6);
        hexBox.setResponder(this::onHexChanged);
        hexBox.setValue(String.format("%06X", startColor));
        addRenderableWidget(hexBox);
    }

    private void onColorChanged() {
        int color = (red << 16) | (green << 8) | blue;
        hexBox.setValue(String.format("%06X", color));
        sendColor(color);
    }

    private void onHexChanged(String value) {
        if (value.length() != 6) {
            return;
        }
        try {
            int color = Integer.parseInt(value, 16);
            red = (color >> 16) & 0xFF;
            green = (color >> 8) & 0xFF;
            blue = color & 0xFF;
            redSlider.setValueSilently(red);
            greenSlider.setValueSilently(green);
            blueSlider.setValueSilently(blue);
            sendColor(color);
        } catch (NumberFormatException ignored) {
        }
    }

    private void sendColor(int color) {
        ChromaNetwork.CHANNEL.sendToServer(new SetDyeColorPacket(color));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int color = (red << 16) | (green << 8) | blue;
        graphics.fill(leftPos + 100, topPos + 96, leftPos + 130, topPos + 116, 0xFF000000 | color);
        graphics.renderOutline(leftPos + 99, topPos + 95, 32, 22, 0xFF1A1A1A);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 6, 0x3F3F3F, false);
        graphics.drawString(font, Component.translatable("container.chroma.dyeing_table.color"), 100, 8, 0x3F3F3F, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private static class ColorSlider extends AbstractSliderButton {

        private final String label;
        private final java.util.function.IntConsumer onChange;
        private boolean silent = false;

        protected ColorSlider(int x, int y, String label, int initialValue, java.util.function.IntConsumer onChange) {
            super(x, y, 70, 14, Component.literal(label + ": " + initialValue), initialValue / 255.0);
            this.label = label;
            this.onChange = onChange;
        }

        public void setValueSilently(int value) {
            this.silent = true;
            this.value = value / 255.0;
            updateMessage();
            this.silent = false;
        }

        @Override
        protected void updateMessage() {
            int intValue = (int) Math.round(value * 255);
            setMessage(Component.literal(label + ": " + intValue));
        }

        @Override
        protected void applyValue() {
            if (silent) {
                return;
            }
            int intValue = (int) Math.round(value * 255);
            onChange.accept(intValue);
        }
    }
}
package com.orbital.chroma.menu;

import com.orbital.chroma.blockentity.DyeingTableBlockEntity;
import com.orbital.chroma.registry.ChromaMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class DyeingTableMenu extends AbstractContainerMenu {

    public final DyeingTableBlockEntity blockEntity;
    private final Level level;
    private final SimpleContainerData colorData = new SimpleContainerData(3);

    public DyeingTableMenu(int containerId, Inventory playerInventory, DyeingTableBlockEntity blockEntity) {
        super(ChromaMenus.DYEING_TABLE.get(), containerId);
        this.blockEntity = blockEntity;
        this.level = playerInventory.player.level();

        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 80, 35));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new net.minecraft.world.inventory.Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new net.minecraft.world.inventory.Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(colorData);
        syncColorData();
    }

    public DyeingTableMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, (DyeingTableBlockEntity) playerInventory.player.level().getBlockEntity(pos));
    }

    public void syncColorData() {
        int color = blockEntity.getPickerColor();
        colorData.set(0, (color >> 16) & 0xFF);
        colorData.set(1, (color >> 8) & 0xFF);
        colorData.set(2, color & 0xFF);
    }

    public int getClientColor() {
        return (colorData.get(0) << 16) | (colorData.get(1) << 8) | colorData.get(2);
    }

    public void setColor(int rgb) {
        blockEntity.setPickerColor(rgb);
        syncColorData();
        blockEntity.applyColorToInput();
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        net.minecraft.world.item.ItemStack result = net.minecraft.world.item.ItemStack.EMPTY;
        net.minecraft.world.inventory.Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            net.minecraft.world.item.ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index == 0) {
                if (!moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return net.minecraft.world.item.ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                    return net.minecraft.world.item.ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) {
                slot.set(net.minecraft.world.item.ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getBlockPos().closerToCenter(player.position(), 8.0);
    }
}
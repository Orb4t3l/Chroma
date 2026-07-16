package com.orbital.chroma.menu;

import com.orbital.chroma.blockentity.DyeingTableBlockEntity;
import com.orbital.chroma.registry.ChromaMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.SlotItemHandler;

public class DyeingTableMenu extends AbstractContainerMenu {

    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 7;
    public static final int INV_ROW_Y = 122;
    public static final int HOTBAR_Y = 180;

    public final DyeingTableBlockEntity blockEntity;
    private final SimpleContainerData colorData = new SimpleContainerData(3);

    public DyeingTableMenu(int containerId, Inventory playerInventory, DyeingTableBlockEntity blockEntity) {
        super(ChromaMenus.DYEING_TABLE.get(), containerId);
        this.blockEntity = blockEntity;

        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, SLOT_X, SLOT_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, INV_ROW_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, HOTBAR_Y));
        }

        addDataSlots(colorData);
        syncColorData();
    }

    public DyeingTableMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory,
                (DyeingTableBlockEntity) playerInventory.player.level().getBlockEntity(pos));
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
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index == 0) {
                if (!moveItemStackTo(stackInSlot, 1, 37, true)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stackInSlot, 0, 1, false)) return ItemStack.EMPTY;
            }
            if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).closerThan(player.position(), 8.0);
    }
}
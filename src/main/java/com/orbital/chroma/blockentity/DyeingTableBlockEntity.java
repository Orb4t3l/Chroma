package com.orbital.chroma.blockentity;

import com.orbital.chroma.api.ColorAPI;
import com.orbital.chroma.api.IDyeable;
import com.orbital.chroma.menu.DyeingTableMenu;
import com.orbital.chroma.registry.ChromaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DyeingTableBlockEntity extends BlockEntity implements MenuProvider {

    private int pickerColor = 0xFFFFFF;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                    && (ColorAPI.isDyeable(blockItem.getBlock()) || stack.getItem() instanceof net.minecraft.world.item.DyeableLeatherItem);
        }
    };

    private final LazyOptional<ItemStackHandler> itemHandlerOptional = LazyOptional.of(() -> itemHandler);

    public DyeingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ChromaBlockEntities.DYEING_TABLE.get(), pos, state);
    }

    public int getPickerColor() {
        return pickerColor;
    }

    public void setPickerColor(int rgb) {
        this.pickerColor = rgb;
        setChanged();
    }

    public void applyColorToInput() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem && ColorAPI.isDyeable(blockItem.getBlock())) {
            ColorAPI.setItemColor(stack, pickerColor);
        } else if (stack.getItem() instanceof net.minecraft.world.item.DyeableLeatherItem dyeable) {
            stack.getOrCreateTagElement("display").putInt("color", pickerColor);
        }
        setChanged();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("PickerColor", pickerColor);
        tag.put("Inventory", itemHandler.serializeNBT());
        return tag;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("PickerColor", pickerColor);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("PickerColor")) {
            pickerColor = tag.getInt("PickerColor");
        }
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerOptional.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.chroma.dyeing_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DyeingTableMenu(containerId, playerInventory, this);
    }
}
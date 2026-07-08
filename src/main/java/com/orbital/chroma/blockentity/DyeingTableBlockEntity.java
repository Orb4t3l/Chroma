package com.orbital.chroma.blockentity;

import com.orbital.chroma.api.ColorAPI;
import com.orbital.chroma.menu.DyeingTableMenu;
import com.orbital.chroma.registry.ChromaBlockEntities;
import com.orbital.chroma.registry.ChromaItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DyeingTableBlockEntity extends BlockEntity implements MenuProvider {

    private int pickerColor = 0xFFFFFF;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            ItemStack stack = getStackInSlot(slot);
            if (!stack.isEmpty()) {
                int read = ColorAPI.getItemColor(stack, -1);
                if (read != -1) pickerColor = read;
            }
            setChanged();
            if (level != null && !level.isClientSide)
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return false;
            if (stack.getItem() instanceof net.minecraft.world.item.DyeableLeatherItem) return true;
            if (ColorAPI.isDyeableItem(stack.getItem())) return true;
            if (stack.is(ItemTags.BANNERS)) return true;
            if (stack.hasTag() && stack.getTag().contains("ChromaColor")) return true;
            if (!(stack.getItem() instanceof BlockItem bi)) return false;
            if (ColorAPI.isDyeable(bi.getBlock())) return true;
            return getConversionTarget(bi.getBlock()) != null;
        }
    };

    private final LazyOptional<ItemStackHandler> lazyHandler = LazyOptional.of(() -> itemHandler);

    public DyeingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ChromaBlockEntities.DYEING_TABLE.get(), pos, state);
    }

    @Nullable
    private static Item getConversionTarget(Block block) {
        BlockState state = block.defaultBlockState();
        if (state.is(BlockTags.WOOL))         return ChromaItems.CHROMA_WOOL.get();
        if (state.is(BlockTags.WOOL_CARPETS)) return ChromaItems.CHROMA_CARPET.get();
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null) return null;
        String path = id.getPath();
        if (path.endsWith("_concrete_powder")) return ChromaItems.CHROMA_CONCRETE_POWDER.get();
        if (path.endsWith("_concrete"))        return ChromaItems.CHROMA_CONCRETE.get();
        if (path.contains("terracotta") && !path.contains("glazed")) return ChromaItems.CHROMA_TERRACOTTA.get();
        if (path.endsWith("_stained_glass"))   return ChromaItems.CHROMA_STAINED_GLASS.get();
        return null;
    }

    private ItemStack coloredCopy(ItemStack original, int color) {
        if (original.getItem() instanceof net.minecraft.world.item.DyeableLeatherItem) {
            ItemStack copy = original.copy();
            copy.getOrCreateTagElement("display").putInt("color", color);
            return copy;
        }
        if (ColorAPI.isDyeableItem(original.getItem())) {
            ItemStack copy = original.copy();
            ColorAPI.applyColorToItem(copy, color);
            return copy;
        }
        if (original.is(ItemTags.BANNERS)) {
            ItemStack banner = new ItemStack(ChromaItems.CHROMA_BANNER.get());
            if (original.hasTag()) banner.setTag(original.getTag().copy());
            ColorAPI.setItemColor(banner, color);
            return banner;
        }
        if (original.hasTag() && original.getTag().contains("ChromaColor")) {
            ItemStack copy = original.copy();
            ColorAPI.setItemColor(copy, color);
            return copy;
        }
        if (original.getItem() instanceof BlockItem bi) {
            if (ColorAPI.isDyeable(bi.getBlock())) {
                ItemStack copy = original.copy();
                ColorAPI.setItemColor(copy, color);
                return copy;
            }
            Item target = getConversionTarget(bi.getBlock());
            if (target != null) {
                ItemStack converted = new ItemStack(target);
                ColorAPI.setItemColor(converted, color);
                return converted;
            }
        }
        return original.copy();
    }

    public int getPickerColor() { return pickerColor; }

    public void setPickerColor(int rgb) {
        this.pickerColor = rgb;
        setChanged();
    }

    public void applyColorToInput() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return;
        ItemStack result = coloredCopy(stack, pickerColor);
        result.setCount(stack.getCount());
        itemHandler.setStackInSlot(0, result);
        setChanged();
    }

    private static final int MAX_GRADIENT_BANDS = 16;

    public List<ItemStack> applyGradient(int colorA, int colorB) {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return List.of();

        int totalCount = stack.getCount();
        int bands = Math.min(totalCount, MAX_GRADIENT_BANDS);
        List<ItemStack> results = new ArrayList<>(bands);

        int rA = (colorA >> 16) & 0xFF, gA = (colorA >> 8) & 0xFF, bA = colorA & 0xFF;
        int rB = (colorB >> 16) & 0xFF, gB = (colorB >> 8) & 0xFF, bB = colorB & 0xFF;

        int baseCountPerBand = totalCount / bands;
        int remainder = totalCount % bands;

        for (int i = 0; i < bands; i++) {
            float t = bands == 1 ? 0f : (float) i / (bands - 1);
            int r = Math.round(rA + (rB - rA) * t);
            int g = Math.round(gA + (gB - gA) * t);
            int b = Math.round(bA + (bB - bA) * t);
            int color = (r << 16) | (g << 8) | b;

            int countForBand = baseCountPerBand + (i < remainder ? 1 : 0);
            if (countForBand <= 0) continue;

            results.add(coloredCopy(stack.copyWithCount(countForBand), color));
        }

        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        setChanged();
        return results;
    }

    public ItemStackHandler getItemHandler() { return itemHandler; }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("PickerColor", pickerColor);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("PickerColor")) pickerColor = tag.getInt("PickerColor");
        if (tag.contains("Inventory")) itemHandler.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("PickerColor", pickerColor);
        tag.put("Inventory", itemHandler.serializeNBT());
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                             @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override public void invalidateCaps() { lazyHandler.invalidate(); }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.chroma.dyeing_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new DyeingTableMenu(id, inv, this);
    }
}
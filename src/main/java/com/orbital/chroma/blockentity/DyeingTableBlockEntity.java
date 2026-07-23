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
import net.minecraft.world.item.Items;
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
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
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
        if (state.is(BlockTags.WOOL)) return ChromaItems.CHROMA_WOOL.get();
        if (state.is(BlockTags.WOOL_CARPETS)) return ChromaItems.CHROMA_CARPET.get();
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null) return null;
        String path = id.getPath();
        if (path.endsWith("_concrete_powder")) return ChromaItems.CHROMA_CONCRETE_POWDER.get();
        if (path.endsWith("_concrete"))        return ChromaItems.CHROMA_CONCRETE.get();
        if (path.contains("terracotta") && !path.contains("glazed")) return ChromaItems.CHROMA_TERRACOTTA.get();
        if (path.endsWith("_stained_glass_pane")) return ChromaItems.CHROMA_STAINED_GLASS_PANE.get();
        if (path.endsWith("_stained_glass"))   return ChromaItems.CHROMA_STAINED_GLASS.get();
        return null;
    }

    /**
     * Reverse of getConversionTarget - given a Chroma item, returns the plain
     * vanilla item it should revert to when "Unchromafy" is used, discarding
     * all color/gradient data.
     */
    @Nullable
    private static Item getReverseTarget(Item chromaItem) {
        if (chromaItem == ChromaItems.CHROMA_WOOL.get())            return Items.WHITE_WOOL;
        if (chromaItem == ChromaItems.CHROMA_CARPET.get())          return Items.WHITE_CARPET;
        if (chromaItem == ChromaItems.CHROMA_CONCRETE.get())        return Items.WHITE_CONCRETE;
        if (chromaItem == ChromaItems.CHROMA_CONCRETE_POWDER.get()) return Items.WHITE_CONCRETE_POWDER;
        if (chromaItem == ChromaItems.CHROMA_TERRACOTTA.get())      return Items.WHITE_TERRACOTTA;
        if (chromaItem == ChromaItems.CHROMA_STAINED_GLASS.get())   return Items.WHITE_STAINED_GLASS;
        if (chromaItem == ChromaItems.CHROMA_STAINED_GLASS_PANE.get()) return Items.WHITE_STAINED_GLASS_PANE;
        if (chromaItem == ChromaItems.CHROMA_BANNER.get())          return Items.WHITE_BANNER;
        return null;
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

    /**
     * Strips a Chroma item back down to its plain vanilla equivalent,
     * discarding color and gradient data entirely. For non-Chroma-specific
     * items (leather armor, compat mod items), just clears the relevant
     * color tags instead of swapping the item type.
     */
    public void unchromafy() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return;

        Item reverseTarget = getReverseTarget(stack.getItem());
        if (reverseTarget != null) {
            ItemStack plain = new ItemStack(reverseTarget, stack.getCount());
            itemHandler.setStackInSlot(0, plain);
            setChanged();
            return;
        }

        if (stack.getItem() instanceof net.minecraft.world.item.DyeableLeatherItem) {
            ItemStack copy = stack.copy();
            if (copy.hasTag() && copy.getTag().contains("display")) {
                copy.getTag().getCompound("display").remove("color");
            }
            itemHandler.setStackInSlot(0, copy);
            setChanged();
            return;
        }

        if (ColorAPI.isDyeableItem(stack.getItem())) {
            // Compat-registered item: no generic "reset" concept exists across
            // arbitrary mods, so just clear whatever tags we control.
            ItemStack copy = stack.copy();
            if (copy.hasTag()) {
                copy.getTag().remove("ChromaColor");
                copy.getTag().remove("ChromaGradientEnd");
            }
            itemHandler.setStackInSlot(0, copy);
            setChanged();
            return;
        }

        if (stack.hasTag() && stack.getTag().contains("ChromaColor")) {
            ItemStack copy = stack.copy();
            copy.getTag().remove("ChromaColor");
            copy.getTag().remove("ChromaGradientEnd");
            if (copy.getTag().contains("BlockEntityTag")) {
                CompoundTag bet = copy.getTag().getCompound("BlockEntityTag");
                bet.remove("Color");
                bet.remove("GradientEnd");
            }
            itemHandler.setStackInSlot(0, copy);
            setChanged();
        }
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
                ItemStack converted = new ItemStack(target, original.getCount());
                ColorAPI.setItemColor(converted, color);
                return converted;
            }
        }
        return original.copy();
    }

    public void applyGradient(int colorA, int colorB) {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if (stack.isEmpty()) return;

        ItemStack result = coloredCopy(stack, colorA);
        result.setCount(stack.getCount());

        if (result.getItem() instanceof net.minecraft.world.item.DyeableLeatherItem) {
            var display = result.getOrCreateTagElement("display");
            display.putInt("color", colorA);
        } else if (ColorAPI.isDyeableItem(result.getItem())) {
            ColorAPI.applyColorToItem(result, colorA);
        } else {
            ColorAPI.setItemGradient(result, colorA, colorB);
        }

        itemHandler.setStackInSlot(0, result);
        setChanged();
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

    @Override
    public void invalidateCaps() { lazyHandler.invalidate(); }

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
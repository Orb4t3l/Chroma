package com.orbital.chroma.recipe;

import com.orbital.chroma.registry.ChromaItems;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.level.Level;

public class ChromaBannerRecipe extends CustomRecipe {

    public ChromaBannerRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int woolCount = 0;
        int stickCount = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() == ChromaItems.CHROMA_WOOL.get()) {
                woolCount++;
            } else if (stack.getItem() == Items.STICK) {
                stickCount++;
            } else {
                return false;
            }
        }
        return woolCount == 6 && stickCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == ChromaItems.CHROMA_WOOL.get()) {
                ItemStack result = new ItemStack(ChromaItems.CHROMA_BANNER.get(), 1);
                if (stack.hasTag()) result.setTag(stack.getTag().copy());
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 7;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return new ItemStack(ChromaItems.CHROMA_BANNER.get(), 1);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return com.orbital.chroma.registry.ChromaRecipeSerializers.BANNER_RECIPE.get();
    }
}
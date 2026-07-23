package com.orbital.chroma.registry;

import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.recipe.ChromaBannerRecipe;
import com.orbital.chroma.recipe.ChromaCarpetRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ChromaRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ChromaMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ChromaCarpetRecipe>> CARPET_RECIPE = REGISTRY.register(
            "chroma_carpet_dye_copy",
            () -> new SimpleCraftingRecipeSerializer<>(ChromaCarpetRecipe::new));

    public static final RegistryObject<RecipeSerializer<ChromaBannerRecipe>> BANNER_RECIPE = REGISTRY.register(
            "chroma_banner_dye_copy",
            () -> new SimpleCraftingRecipeSerializer<>(ChromaBannerRecipe::new));

    private ChromaRecipeSerializers() {}
}
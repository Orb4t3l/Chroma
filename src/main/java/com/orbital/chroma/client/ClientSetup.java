package com.orbital.chroma.client;

import com.orbital.chroma.api.IDyeable;
import com.orbital.chroma.api.ColorAPI;
import com.orbital.chroma.registry.ChromaBlocks;
import com.orbital.chroma.registry.ChromaItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.model.data.ModelData;
import com.orbital.chroma.ChromaMod;
import com.orbital.chroma.registry.ChromaMenus;

@Mod.EventBusSubscriber(modid = ChromaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ChromaMenus.DYEING_TABLE.get(), DyeingTableScreen::new);

            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(
                    ChromaBlocks.CHROMA_STAINED_GLASS.get(), RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        Block[] chromaBlocks = {
                ChromaBlocks.CHROMA_WOOL.get(),
                ChromaBlocks.CHROMA_CARPET.get(),
                ChromaBlocks.CHROMA_CONCRETE.get(),
                ChromaBlocks.CHROMA_CONCRETE_POWDER.get(),
                ChromaBlocks.CHROMA_TERRACOTTA.get(),
                ChromaBlocks.CHROMA_STAINED_GLASS.get()
        };

        event.register((state, level, pos, tintIndex) -> {
            if (level != null && pos != null) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof IDyeable dyeable) {
                    return dyeable.getColor();
                }
            }
            return 0xFFFFFF;
        }, chromaBlocks);
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        Item[] chromaItems = {
                ChromaItems.CHROMA_WOOL.get(),
                ChromaItems.CHROMA_CARPET.get(),
                ChromaItems.CHROMA_CONCRETE.get(),
                ChromaItems.CHROMA_CONCRETE_POWDER.get(),
                ChromaItems.CHROMA_TERRACOTTA.get(),
                ChromaItems.CHROMA_STAINED_GLASS.get()
        };

        event.register((stack, tintIndex) -> {
            if (stack.hasTag() && stack.getTag().contains("ChromaColor")) {
                return stack.getTag().getInt("ChromaColor");
            }
            return 0xFFFFFF;
        }, chromaItems);
    }
}
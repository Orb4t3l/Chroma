package com.orbital.chroma.item;

import com.orbital.chroma.client.DyeingTableItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ChromaDyeingTableItem extends BlockItem {

    public ChromaDyeingTableItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DyeingTableItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    var mc = net.minecraft.client.Minecraft.getInstance();
                    renderer = new DyeingTableItemRenderer(
                            mc.getBlockEntityRenderDispatcher(),
                            mc.getEntityModels());
                }
                return renderer;
            }
        });
    }
}
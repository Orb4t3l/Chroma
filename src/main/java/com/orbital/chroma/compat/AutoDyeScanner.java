package com.orbital.chroma.compat;

import com.orbital.chroma.api.ColorAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class AutoDyeScanner {

    private static final Logger LOGGER = LogManager.getLogger("Chroma/AutoDyeScanner");

    private static final String[] DYE_PREFIXES = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime",
            "pink", "gray", "light_gray", "cyan", "purple", "blue",
            "brown", "green", "red", "black"
    };

    private static final int MIN_VARIANTS = 3;

    // Base names (after stripping color prefix) that are either:
    // - already handled natively by Chroma with correct NBT, OR
    // - clearly not user-dyeable (flora, minerals, etc.)
    private static final Set<String> BASE_NAME_BLOCKLIST = Set.of(
            // vanilla blocks Chroma handles natively
            "wool", "carpet", "concrete", "concrete_powder",
            "terracotta", "stained_glass", "stained_glass_pane",
            "banner", "wall_banner", "bed", "glazed_terracotta", "shulker_box",
            // vanilla dyes and dye-adjacent items
            "dye", "candle", "candle_cake",
            // flora that starts with a color name
            "tulip", "mushroom", "mushroom_block", "mushroom_stem",
            "orchid", "allium", "daisy", "poppy",
            // materials / minerals
            "nether_brick", "nether_bricks", "sandstone",
            // misc that would be false positives
            "wool_carpet", "sand"
    );

    private static final Set<String> NAMESPACE_BLOCKLIST = Set.of(
            "minecraft", "forge", "neoforge"
    );

    private AutoDyeScanner() {}

    public static void scan() {
        int leatherCount = scanDyeableLeatherItems();
        int groupCount   = scanColorPrefixGroups();

        if (leatherCount + groupCount > 0) {
            LOGGER.info("[Chroma] Auto-compat complete: {} DyeableLeatherItem(s), {} color-group item(s)",
                    leatherCount, groupCount);
        } else {
            LOGGER.debug("[Chroma] Auto-compat scan found nothing new to register");
        }
    }

    // automatically, catching modded leather armor, horse armor, etc.
    private static int scanDyeableLeatherItems() {
        int count = 0;
        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (NAMESPACE_BLOCKLIST.contains(id.getNamespace())) continue;

            Item item = entry.getValue();
            if (ColorAPI.isDyeableItem(item)) continue;

            if (item instanceof net.minecraft.world.item.DyeableLeatherItem) {
                ColorAPI.registerCustomDyeableItem(item,
                        (stack, rgb) -> stack.getOrCreateTagElement("display").putInt("color", rgb),
                        stack -> {
                            if (stack.hasTag() && stack.getTag().contains("display")) {
                                var d = stack.getTag().getCompound("display");
                                if (d.contains("color")) return d.getInt("color");
                            }
                            return 0xFFFFFF;
                        });
                LOGGER.debug("[Chroma] Auto-registered DyeableLeatherItem: {}", id);
                count++;
            }
        }
        return count;
    }

    // Group them by namespace:baseName. If a group has MIN_VARIANTS or more
    // distinct DyeColor-named variants, it's almost certainly a color series.
    private static int scanColorPrefixGroups() {
        Map<String, Map<String, Item>> groups = new LinkedHashMap<>();

        for (var entry : ForgeRegistries.ITEMS.getEntries()) {
            ResourceLocation id = entry.getKey().location();
            if (NAMESPACE_BLOCKLIST.contains(id.getNamespace())) continue;
            if (ColorAPI.isDyeableItem(entry.getValue())) continue;

            String path = id.getPath();
            for (String prefix : DYE_PREFIXES) {
                if (!path.startsWith(prefix + "_")) continue;
                String base = path.substring(prefix.length() + 1);
                if (BASE_NAME_BLOCKLIST.contains(base)) break;

                String groupKey = id.getNamespace() + ":" + base;
                groups.computeIfAbsent(groupKey, k -> new LinkedHashMap<>())
                        .put(prefix, entry.getValue());
                break;
            }
        }

        int count = 0;
        for (var groupEntry : groups.entrySet()) {
            Map<String, Item> variants = groupEntry.getValue();
            if (variants.size() < MIN_VARIANTS) continue;

            LOGGER.info("[Chroma] Auto-detected color group '{}' with {} variants",
                    groupEntry.getKey(), variants.size());

            for (var variantEntry : variants.entrySet()) {
                Item item = variantEntry.getValue();
                if (ColorAPI.isDyeableItem(item)) continue;

                int defaultRgb = DyeColor.byName(variantEntry.getKey(), DyeColor.WHITE).getFireworkColor();
                final int rgb  = defaultRgb;
                ColorAPI.registerCustomDyeableItem(item,
                        (stack, newRgb) -> stack.getOrCreateTagElement("display").putInt("color", newRgb),
                        stack -> {
                            if (stack.hasTag() && stack.getTag().contains("display")) {
                                var d = stack.getTag().getCompound("display");
                                if (d.contains("color")) return d.getInt("color");
                            }
                            return rgb;
                        });
                count++;
            }
        }
        return count;
    }
}
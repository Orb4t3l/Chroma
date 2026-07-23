package com.orbital.chroma.network;

import com.orbital.chroma.ChromaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ChromaNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ChromaMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, SetDyeColorPacket.class,
                SetDyeColorPacket::encode,
                SetDyeColorPacket::new,
                SetDyeColorPacket::handle);

        CHANNEL.registerMessage(packetId++, ApplyGradientPacket.class,
                ApplyGradientPacket::encode,
                ApplyGradientPacket::new,
                ApplyGradientPacket::handle);

        CHANNEL.registerMessage(packetId++, UnchromafyPacket.class,
                UnchromafyPacket::encode,
                UnchromafyPacket::new,
                UnchromafyPacket::handle);
    }

    private ChromaNetwork() {}
}
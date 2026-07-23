package com.orbital.chroma.network;

import com.orbital.chroma.menu.DyeingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnchromafyPacket {

    public UnchromafyPacket() {}

    public UnchromafyPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (!(player.containerMenu instanceof DyeingTableMenu menu)) return;
            menu.blockEntity.unchromafy();
        });
        context.setPacketHandled(true);
    }
}
package com.orbital.chroma.network;

import com.orbital.chroma.menu.DyeingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetDyeColorPacket {

    private final int color;

    public SetDyeColorPacket(int color) {
        this.color = color;
    }

    public SetDyeColorPacket(FriendlyByteBuf buf) {
        this.color = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(color);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            net.minecraft.server.level.ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            if (player.containerMenu instanceof DyeingTableMenu menu) {
                menu.setColor(color);
            }
        });
        context.setPacketHandled(true);
    }
}
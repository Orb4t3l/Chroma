package com.orbital.chroma.network;

import com.orbital.chroma.menu.DyeingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ApplyGradientPacket {

    private final int colorA;
    private final int colorB;

    public ApplyGradientPacket(int colorA, int colorB) {
        this.colorA = colorA;
        this.colorB = colorB;
    }

    public ApplyGradientPacket(FriendlyByteBuf buf) {
        this.colorA = buf.readInt();
        this.colorB = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(colorA);
        buf.writeInt(colorB);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            if (!(player.containerMenu instanceof DyeingTableMenu menu)) return;

            menu.blockEntity.applyGradient(colorA, colorB);
        });
        context.setPacketHandled(true);
    }
}
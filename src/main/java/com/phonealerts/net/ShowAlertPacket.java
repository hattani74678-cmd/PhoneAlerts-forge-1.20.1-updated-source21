package com.phonealerts.net;

import com.phonealerts.AlertData;
import com.phonealerts.client.ClientAlertManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server -> Client: display a banner + play the tone. */
public record ShowAlertPacket(AlertData data) {

    public static void encode(ShowAlertPacket p, FriendlyByteBuf buf) { p.data.write(buf); }

    public static ShowAlertPacket decode(FriendlyByteBuf buf) { return new ShowAlertPacket(AlertData.read(buf)); }

    public static void handle(ShowAlertPacket p, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAlertManager.receive(p.data)));
        ctx.setPacketHandled(true);
    }
}

package com.phonealerts.net;

import com.phonealerts.AlertConfig;
import com.phonealerts.AlertService;
import com.phonealerts.AlertType;
import com.phonealerts.Severity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Client -> Server: the control panel's "SEND" button. */
public record SendAlertRequestPacket(AlertType type, Severity severity, String title, String body,
                                     int radius, boolean nearbyOnly, boolean applyWeather) {

    public static void encode(SendAlertRequestPacket p, FriendlyByteBuf buf) {
        buf.writeByte(p.type.ordinal());
        buf.writeByte(p.severity.ordinal());
        buf.writeUtf(p.title, 80);
        buf.writeUtf(p.body, 200);
        buf.writeVarInt(p.radius);
        buf.writeBoolean(p.nearbyOnly);
        buf.writeBoolean(p.applyWeather);
    }

    public static SendAlertRequestPacket decode(FriendlyByteBuf buf) {
        return new SendAlertRequestPacket(AlertType.byOrdinal(buf.readByte()), Severity.byOrdinal(buf.readByte()),
                buf.readUtf(80), buf.readUtf(200), buf.readVarInt(), buf.readBoolean(), buf.readBoolean());
    }

    public static void handle(SendAlertRequestPacket p, Supplier<NetworkEvent.Context> ctxSup) {
        NetworkEvent.Context ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null) return;
            if (!sender.hasPermissions(AlertConfig.PERMISSION_LEVEL.get())) {
                sender.sendSystemMessage(Component.translatable("phonealerts.msg.no_permission"));
                return;
            }
            int n = AlertService.dispatch(sender, p.type, p.severity, p.title, p.body,
                    p.radius, p.nearbyOnly, p.applyWeather);
            sender.sendSystemMessage(Component.translatable("phonealerts.msg.sent", n));
        });
        ctx.setPacketHandled(true);
    }
}

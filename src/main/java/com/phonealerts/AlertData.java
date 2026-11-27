package com.phonealerts;

import net.minecraft.network.FriendlyByteBuf;

/** Pure data object sent from server to clients. */
public record AlertData(AlertType type, Severity severity, String title, String body,
                        boolean hasZone, double zx, double zz, int zr, String dimension) {

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeByte(severity.ordinal());
        buf.writeUtf(title, 256);
        buf.writeUtf(body, 512);
        buf.writeBoolean(hasZone);
        buf.writeDouble(zx);
        buf.writeDouble(zz);
        buf.writeVarInt(zr);
        buf.writeUtf(dimension, 128);
    }

    public static AlertData read(FriendlyByteBuf buf) {
        return new AlertData(AlertType.byOrdinal(buf.readByte()), Severity.byOrdinal(buf.readByte()),
                buf.readUtf(256), buf.readUtf(512), buf.readBoolean(),
                buf.readDouble(), buf.readDouble(), buf.readVarInt(), buf.readUtf(128));
    }
}

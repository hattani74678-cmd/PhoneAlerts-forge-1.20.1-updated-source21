package com.phonealerts.net;

import com.phonealerts.AlertData;
import com.phonealerts.PhoneAlerts;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PhoneAlerts.MODID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SendAlertRequestPacket.class,
                SendAlertRequestPacket::encode, SendAlertRequestPacket::decode, SendAlertRequestPacket::handle);
        CHANNEL.registerMessage(id++, ShowAlertPacket.class,
                ShowAlertPacket::encode, ShowAlertPacket::decode, ShowAlertPacket::handle);
    }

    public static void sendToPlayer(ServerPlayer p, AlertData data) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new ShowAlertPacket(data));
    }
}

package com.phonealerts;

import com.phonealerts.net.ModNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class AlertService {

    /**
     * @return number of players who received the alert
     */
    public static int dispatch(ServerPlayer origin, AlertType type, Severity severity, String title, String body,
                               int radius, boolean nearbyOnly, boolean applyWeather) {
        MinecraftServer server = origin.server;
        int r = Math.max(16, Math.min(radius, AlertConfig.MAX_RADIUS.get()));
        String dim = origin.level().dimension().location().toString();

        AlertData data = new AlertData(type, severity, title.trim(), body.trim(),
                type.hasZone, origin.getX(), origin.getZ(), r, dim);

        int count = 0;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (nearbyOnly) {
                if (p.level() != origin.level()) continue;
                double dx = p.getX() - origin.getX(), dz = p.getZ() - origin.getZ();
                if (dx * dx + dz * dz > (double) r * r) continue;
            }
            ModNetwork.sendToPlayer(p, data);
            count++;
        }

        if (applyWeather && AlertConfig.ALLOW_WEATHER_CONTROL.get()) applyWeather(server, type);
        return count;
    }

    private static void applyWeather(MinecraftServer server, AlertType type) {
        ServerLevel overworld = server.overworld();
        switch (type.weather) {
            case RAIN -> overworld.setWeatherParameters(0, 6000, true, false);
            case THUNDER -> overworld.setWeatherParameters(0, 6000, true, true);
            case CLEAR -> overworld.setWeatherParameters(6000, 0, false, false);
            default -> { }
        }
    }
}

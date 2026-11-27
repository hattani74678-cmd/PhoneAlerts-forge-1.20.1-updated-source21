package com.phonealerts;

import net.minecraftforge.common.ForgeConfigSpec;

public class AlertConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.IntValue PERMISSION_LEVEL;
    public static final ForgeConfigSpec.BooleanValue ALLOW_WEATHER_CONTROL;
    public static final ForgeConfigSpec.IntValue MAX_RADIUS;

    public static final ForgeConfigSpec.DoubleValue VOLUME;
    public static final ForgeConfigSpec.IntValue BANNER_SECONDS;
    public static final ForgeConfigSpec.DoubleValue BANNER_SCALE;
    public static final ForgeConfigSpec.BooleanValue DARK_STYLE;

    static {
        ForgeConfigSpec.Builder c = new ForgeConfigSpec.Builder();
        c.push("server");
        PERMISSION_LEVEL = c.comment("Permission level required to send alerts (0 = everyone, 2 = ops/cheats, 4 = owner).")
                .defineInRange("permissionLevel", 2, 0, 4);
        ALLOW_WEATHER_CONTROL = c.comment("Allow alerts to actually change the world's weather.")
                .define("allowWeatherControl", true);
        MAX_RADIUS = c.comment("Maximum zone / nearby radius in blocks.")
                .defineInRange("maxRadius", 5000, 16, 30000);
        c.pop();
        COMMON_SPEC = c.build();

        ForgeConfigSpec.Builder k = new ForgeConfigSpec.Builder();
        k.push("client");
        VOLUME = k.comment("Alert tone volume.").defineInRange("volume", 1.0, 0.0, 1.0);
        BANNER_SECONDS = k.comment("How long a banner stays on screen.").defineInRange("bannerSeconds", 6, 2, 20);
        BANNER_SCALE = k.comment("Banner size multiplier.").defineInRange("bannerScale", 1.0, 0.6, 2.0);
        DARK_STYLE = k.comment("true = dark glass banner, false = light glass banner.").define("darkStyle", true);
        k.pop();
        CLIENT_SPEC = k.build();
    }
}

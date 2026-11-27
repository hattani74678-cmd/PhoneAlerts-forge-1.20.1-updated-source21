package com.phonealerts;

public enum Severity {
    INFO("info", 0xFF0A84FF),
    WARNING("warning", 0xFFFF9F0A),
    CRITICAL("critical", 0xFFFF453A),
    SAFE("safe", 0xFF30D158);

    public final String id;
    public final int color;

    Severity(String id, int color) { this.id = id; this.color = color; }

    public String langKey() { return "phonealerts.severity." + id; }

    public static Severity byOrdinal(int i) {
        Severity[] v = values();
        return v[Math.floorMod(i, v.length)];
    }
}

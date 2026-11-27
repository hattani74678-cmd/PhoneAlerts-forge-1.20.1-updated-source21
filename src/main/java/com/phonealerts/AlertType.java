package com.phonealerts;

public enum AlertType {
    HOSTILE_THREAT("hostile", "!", Severity.CRITICAL, WeatherAction.NONE, true),
    AREA_ZONE("zone", "#", Severity.WARNING, WeatherAction.NONE, true),
    ALL_CLEAR("clear", "+", Severity.SAFE, WeatherAction.CLEAR, false),
    RAIN("rain", "~", Severity.WARNING, WeatherAction.RAIN, false),
    THUNDERSTORM("storm", "%", Severity.CRITICAL, WeatherAction.THUNDER, false),
    EVACUATE("evac", ">", Severity.CRITICAL, WeatherAction.NONE, true),
    PROJECTILES("projectiles", "⚠", Severity.CRITICAL, WeatherAction.NONE, true),
    CUSTOM("custom", "i", Severity.INFO, WeatherAction.NONE, false);

    public enum WeatherAction { NONE, RAIN, THUNDER, CLEAR }

    public final String id;
    public final String glyph;
    public final Severity defaultSeverity;
    public final WeatherAction weather;
    /** Types that mark a location on the map (sender position + radius). */
    public final boolean hasZone;

    AlertType(String id, String glyph, Severity sev, WeatherAction w, boolean zone) {
        this.id = id; this.glyph = glyph; this.defaultSeverity = sev; this.weather = w; this.hasZone = zone;
    }

    public String typeKey() { return "phonealerts.type." + id; }
    public String titleKey() { return "phonealerts.alert." + id + ".title"; }
    public String bodyKey() { return "phonealerts.alert." + id + ".body"; }

    public static AlertType byOrdinal(int i) {
        AlertType[] v = values();
        return v[Math.floorMod(i, v.length)];
    }

    public static AlertType byId(String id) {
        for (AlertType t : values()) if (t.id.equals(id)) return t;
        return null;
    }
}

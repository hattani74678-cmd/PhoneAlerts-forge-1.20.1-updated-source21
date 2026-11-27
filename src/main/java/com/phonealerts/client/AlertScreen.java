package com.phonealerts.client;

import com.phonealerts.AlertType;
import com.phonealerts.Severity;
import com.phonealerts.net.ModNetwork;
import com.phonealerts.net.SendAlertRequestPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AlertScreen extends Screen {
    private static final int COL_W = 200;

    // remembered between openings
    private static AlertType lastType = AlertType.HOSTILE_THREAT;
    private static Severity lastSeverity = AlertType.HOSTILE_THREAT.defaultSeverity;
    private static boolean lastNearby = false;
    private static boolean lastWeather = false;
    private static double lastSlider = 0.1;

    private AlertType type = lastType;
    private Severity severity = lastSeverity;
    private boolean nearbyOnly = lastNearby;
    private boolean weather = lastWeather;
    private double slider = lastSlider;

    private Button typeBtn, sevBtn, scopeBtn, weatherBtn;
    private RadiusSlider radiusSlider;
    private EditBox titleBox, bodyBox;

    public AlertScreen() { super(Component.translatable("phonealerts.gui.title")); }

    private int radius() { return 16 + (int) Math.round(slider * 984); }

    @Override
    protected void init() {
        int left = width / 2 - COL_W - 5;
        int right = width / 2 + 5;
        int y0 = 30;

        typeBtn = addRenderableWidget(Button.builder(Component.empty(), b -> {
            type = AlertType.byOrdinal(type.ordinal() + 1);
            severity = type.defaultSeverity;
            weather = type.weather != AlertType.WeatherAction.NONE;
            refresh();
        }).bounds(left, y0, COL_W, 20).build());

        sevBtn = addRenderableWidget(Button.builder(Component.empty(), b -> {
            severity = Severity.byOrdinal(severity.ordinal() + 1);
            refresh();
        }).bounds(left, y0 + 24, COL_W, 20).build());

        scopeBtn = addRenderableWidget(Button.builder(Component.empty(), b -> {
            nearbyOnly = !nearbyOnly;
            refresh();
        }).bounds(left, y0 + 48, COL_W, 20).build());

        radiusSlider = addRenderableWidget(new RadiusSlider(left, y0 + 72, COL_W, 20, slider));

        weatherBtn = addRenderableWidget(Button.builder(Component.empty(), b -> {
            weather = !weather;
            refresh();
        }).bounds(right, y0, COL_W, 20).build());

        titleBox = new EditBox(font, right, y0 + 24, COL_W, 20, Component.empty());
        titleBox.setMaxLength(60);
        titleBox.setHint(Component.translatable("phonealerts.gui.title_hint"));
        addRenderableWidget(titleBox);

        bodyBox = new EditBox(font, right, y0 + 48, COL_W, 20, Component.empty());
        bodyBox.setMaxLength(160);
        bodyBox.setHint(Component.translatable("phonealerts.gui.body_hint"));
        addRenderableWidget(bodyBox);

        addRenderableWidget(Button.builder(Component.translatable("phonealerts.gui.send"), b -> send())
                .bounds(left, y0 + 100, COL_W, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("phonealerts.gui.close"), b -> onClose())
                .bounds(right, y0 + 100, COL_W, 20).build());

        refresh();
    }

    private void refresh() {
        typeBtn.setMessage(Component.translatable("phonealerts.gui.type", Component.translatable(type.typeKey())));
        sevBtn.setMessage(Component.translatable("phonealerts.gui.severity", Component.translatable(severity.langKey())));
        scopeBtn.setMessage(Component.translatable(nearbyOnly ? "phonealerts.gui.scope.near" : "phonealerts.gui.scope.all"));
        weatherBtn.setMessage(Component.translatable(weather ? "phonealerts.gui.weather.on" : "phonealerts.gui.weather.off"));
        weatherBtn.active = type.weather != AlertType.WeatherAction.NONE;
        radiusSlider.active = type.hasZone || nearbyOnly;
        lastType = type; lastSeverity = severity; lastNearby = nearbyOnly; lastWeather = weather; lastSlider = slider;
    }

    private void send() {
        boolean w = weather && type.weather != AlertType.WeatherAction.NONE;
        ModNetwork.CHANNEL.sendToServer(new SendAlertRequestPacket(type, severity,
                titleBox.getValue(), bodyBox.getValue(), radius(), nearbyOnly, w));
        onClose();
    }

    private ClientAlert previewAlert() {
        Component title = titleBox.getValue().isBlank()
                ? Component.translatable(type.titleKey()) : Component.literal(titleBox.getValue());
        Component body = bodyBox.getValue().isBlank()
                ? Component.translatable(type.bodyKey()) : Component.literal(bodyBox.getValue());
        Component zone = type.hasZone ? Component.translatable("phonealerts.zone.inside", radius()) : null;
        return new ClientAlert(type, severity, title, body, zone);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        g.drawCenteredString(font, title, width / 2, 10, 0xFFFFFF);
        super.render(g, mx, my, pt);

        int py = 30 + 130;
        g.drawCenteredString(font, Component.translatable("phonealerts.gui.preview"), width / 2, py, 0xAAAAAA);
        var pose = g.pose();
        pose.pushPose();
        pose.translate(width / 2f - AlertBanner.WIDTH / 2f, py + 12, 0);
        AlertBanner.draw(g, font, previewAlert());
        pose.popPose();
    }

    private class RadiusSlider extends AbstractSliderButton {
        RadiusSlider(int x, int y, int w, int h, double value) {
            super(x, y, w, h, Component.empty(), value);
            updateMessage();
        }
        @Override protected void updateMessage() {
            setMessage(Component.translatable("phonealerts.gui.radius", 16 + (int) Math.round(value * 984)));
        }
        @Override protected void applyValue() { slider = value; lastSlider = value; }
    }
}

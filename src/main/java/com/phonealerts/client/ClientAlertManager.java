package com.phonealerts.client;

import com.phonealerts.AlertConfig;
import com.phonealerts.AlertData;
import com.phonealerts.ModSounds;
import com.phonealerts.Severity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;

import java.util.ArrayDeque;
import java.util.Deque;

public class ClientAlertManager {
    private static final long SLIDE_MS = 380;

    private static final Deque<ClientAlert> QUEUE = new ArrayDeque<>();
    private static ClientAlert current;
    private static long startMs;
    private static long secondToneAt = -1;

    public static void receive(AlertData d) {
        Minecraft mc = Minecraft.getInstance();

        Component title = d.title().isEmpty() ? Component.translatable(d.type().titleKey()) : Component.literal(d.title());
        Component body = d.body().isEmpty() ? Component.translatable(d.type().bodyKey()) : Component.literal(d.body());

        Component zoneLine = null;
        if (d.hasZone() && mc.player != null && mc.level != null
                && mc.level.dimension().location().toString().equals(d.dimension())) {
            double dx = mc.player.getX() - d.zx(), dz = mc.player.getZ() - d.zz();
            double dist = Math.sqrt(dx * dx + dz * dz);
            zoneLine = dist <= d.zr()
                    ? Component.translatable("phonealerts.zone.inside", d.zr())
                    : Component.translatable("phonealerts.zone.outside", (int) (dist - d.zr()), d.zr());
        }

        QUEUE.add(new ClientAlert(d.type(), d.severity(), title, body, zoneLine));
    }

    private static void playTone() {
        float vol = AlertConfig.VOLUME.get().floatValue();
        if (vol <= 0f) return;
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(ModSounds.ALERT_TONE.get(), 1.0F, vol));
    }

    public static void render(GuiGraphics g, int screenW, int screenH) {
        long now = System.currentTimeMillis();
        Minecraft mc = Minecraft.getInstance();

        if (current == null && !QUEUE.isEmpty()) {
            current = QUEUE.poll();
            startMs = now;
            playTone();
            secondToneAt = current.severity() == Severity.CRITICAL ? now + 650 : -1;
        }
        if (current == null) return;

        if (secondToneAt > 0 && now >= secondToneAt) { playTone(); secondToneAt = -1; }

        long hold = AlertConfig.BANNER_SECONDS.get() * 1000L;
        long t = now - startMs;
        if (t > SLIDE_MS * 2 + hold) { current = null; return; }

        double p;                                   // 0 = hidden above screen, 1 = fully shown
        if (t < SLIDE_MS) { double x = t / (double) SLIDE_MS; p = 1 - Math.pow(1 - x, 3); }
        else if (t < SLIDE_MS + hold) p = 1;
        else { double x = (t - SLIDE_MS - hold) / (double) SLIDE_MS; p = 1 - x * x * x; }

        float scale = AlertConfig.BANNER_SCALE.get().floatValue();
        int h = AlertBanner.height(mc.font, current);
        float fullH = (h + 6) * scale;
        float x = 12f * scale; // compact alert card anchored to the left edge
        float y = (float) (-fullH + (fullH + 4 * scale) * p);

        var pose = g.pose();
        pose.pushPose();
        pose.translate(x, y, 400);
        pose.scale(scale, scale, 1f);
        AlertBanner.draw(g, mc.font, current);
        pose.popPose();
    }
}

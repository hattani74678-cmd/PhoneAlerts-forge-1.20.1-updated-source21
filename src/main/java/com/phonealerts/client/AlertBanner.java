package com.phonealerts.client;

import com.phonealerts.AlertConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

/** Draws an iOS-style notification banner. Origin (0,0) = top-left of the banner. */
public class AlertBanner {
    public static final int WIDTH = 200;
    private static final int PAD = 8;
    private static final int ICON = 20;
    private static final int TEXT_X = PAD + ICON + 6;
    private static final int TEXT_W = WIDTH - TEXT_X - PAD;
    private static final int MAX_LINES = 3;

    private static List<FormattedCharSequence> bodyLines(Font font, ClientAlert a) {
        List<FormattedCharSequence> lines = new ArrayList<>(font.split(a.body(), TEXT_W));
        if (a.zoneLine() != null) lines.addAll(font.split(a.zoneLine(), TEXT_W));
        if (lines.size() > MAX_LINES + 1) return lines.subList(0, MAX_LINES + 1);
        return lines;
    }

    public static int height(Font font, ClientAlert a) {
        return PAD + 10 + 11 + bodyLines(font, a).size() * 9 + PAD - 1;
    }

    public static void roundedRect(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        for (int i = 0; i < r; i++) {
            int dy = r - i;
            int inset = r - (int) Math.round(Math.sqrt((double) r * r - (double) dy * dy));
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            g.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
        g.fill(x, y + r, x + w, y + h - r, color);
    }

    public static void draw(GuiGraphics g, Font font, ClientAlert a) {
        boolean dark = AlertConfig.DARK_STYLE.get();
        int h = height(font, a);

        int bg = dark ? 0xEB2C2C2E : 0xEBF2F2F7;
        int border = dark ? 0x40FFFFFF : 0x40000000;
        int nameColor = dark ? 0xFF9A9AA0 : 0xFF6C6C70;
        int titleColor = dark ? 0xFFFFFFFF : 0xFF000000;
        int bodyColor = dark ? 0xFFE5E5EA : 0xFF1C1C1E;

        roundedRect(g, 1, 2, WIDTH, h, 9, 0x40000000);          // soft shadow
        roundedRect(g, 0, 0, WIDTH, h, 9, border);               // hairline border
        roundedRect(g, 1, 1, WIDTH - 2, h - 2, 8, bg);           // glass body

        // app icon
        roundedRect(g, PAD, PAD, ICON, ICON, 5, a.severity().color);
        roundedRect(g, PAD, PAD, ICON, 9, 5, 0x22FFFFFF);       // subtle gloss
        String glyph = a.type().glyph;
        g.drawString(font, Component.literal(glyph).withStyle(ChatFormatting.BOLD),
                PAD + (ICON - font.width(glyph)) / 2 - 0, PAD + 6, 0xFFFFFFFF, false);

        // header row: app name + "now"
        var pose = g.pose();
        pose.pushPose();
        pose.translate(TEXT_X, PAD - 1, 0);
        pose.scale(0.75f, 0.75f, 1f);
        g.drawString(font, Component.translatable("phonealerts.app_name"), 0, 0, nameColor, false);
        pose.popPose();

        Component now = Component.translatable("phonealerts.now");
        pose.pushPose();
        pose.translate(WIDTH - PAD - font.width(now) * 0.75f, PAD - 1, 0);
        pose.scale(0.75f, 0.75f, 1f);
        g.drawString(font, now, 0, 0, nameColor, false);
        pose.popPose();

        // title (bold)
        g.drawString(font, a.title().copy().withStyle(ChatFormatting.BOLD), TEXT_X, PAD + 9, titleColor, false);

        // body
        int y = PAD + 20;
        for (FormattedCharSequence line : bodyLines(font, a)) {
            g.drawString(font, line, TEXT_X, y, bodyColor, false);
            y += 9;
        }
    }
}

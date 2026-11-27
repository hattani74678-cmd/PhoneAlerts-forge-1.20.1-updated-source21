package com.phonealerts.client;

import com.phonealerts.PhoneAlerts;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

public class ClientSetup {

    public static final KeyMapping OPEN_PANEL = new KeyMapping("key.phonealerts.open_panel",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.phonealerts");

    @Mod.EventBusSubscriber(modid = PhoneAlerts.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void keys(RegisterKeyMappingsEvent e) { e.register(OPEN_PANEL); }

        @SubscribeEvent
        public static void overlays(RegisterGuiOverlaysEvent e) {
            e.registerAboveAll("alert_banner", (gui, g, partialTick, w, h) -> ClientAlertManager.render(g, w, h));
        }
    }

    @Mod.EventBusSubscriber(modid = PhoneAlerts.MODID, value = Dist.CLIENT)
    public static class ForgeBus {
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            Minecraft mc = Minecraft.getInstance();
            while (OPEN_PANEL.consumeClick()) {
                if (mc.player != null && mc.screen == null) mc.setScreen(new AlertScreen());
            }
        }
    }
}

package com.phonealerts;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, PhoneAlerts.MODID);

    public static final RegistryObject<SoundEvent> ALERT_TONE = SOUNDS.register("alert_tone",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(PhoneAlerts.MODID, "alert_tone")));
}

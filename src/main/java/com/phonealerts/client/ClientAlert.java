package com.phonealerts.client;

import com.phonealerts.AlertType;
import com.phonealerts.Severity;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/** Fully resolved alert ready to be drawn. */
public record ClientAlert(AlertType type, Severity severity, Component title, Component body,
                          @Nullable Component zoneLine) {}

package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.mixin.KeyBindingEntryAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Predicate;

public enum DisplayMode {
    ALL(keyEntry -> true), NONE(keyEntry -> {
        return ((KeyBindingEntryAccessor) keyEntry).binding().isUnbound();
    }), CONFLICTS(keyEntry -> {
        for(KeyBinding key : MinecraftClient.getInstance().options.keysAll) {
            if(!key.getTranslationKey().equals(((KeyBindingEntryAccessor) keyEntry).binding().getTranslationKey()) || key.isUnbound()) {
                if(key.equals(((KeyBindingEntryAccessor) keyEntry).binding())) {
                    return true;
                }
            }
        }
        return false;
    });
    
    private final Predicate<ControlsListWidget.KeyBindingEntry> pred;
    
    
    DisplayMode(Predicate<ControlsListWidget.KeyBindingEntry> pred) {
        this.pred = pred;
    }
    
    
    public Predicate<ControlsListWidget.KeyBindingEntry> getPred() {
        return pred;
    }
}

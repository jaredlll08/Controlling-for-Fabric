package com.blamejared.controlling.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.function.Predicate;

public enum DisplayType {
    ALL(keyEntry -> true), CONFLICTS(keyEntry -> {
        if(keyEntry.binding.isUnbound()) {
            return false;
        }
        for(KeyBinding keyBinding : MinecraftClient.getInstance().options.keysAll) {
            if(!keyBinding.getTranslationKey().equals(keyEntry.binding.getTranslationKey())) {
                if(keyBinding.getBoundKeyTranslationKey().equals(keyEntry.binding.getBoundKeyTranslationKey())) {
                    return true;
                }
            }
        }
        return false;
    }), UNBOUND(keyEntry -> {
        return keyEntry.binding.isUnbound();
    });
    
    private final Predicate<KeyBindingListWidgetNew.KeyEntry> pred;
    
    
    DisplayType(Predicate<KeyBindingListWidgetNew.KeyEntry> pred) {
        this.pred = pred;
    }
    
    
    public Predicate<KeyBindingListWidgetNew.KeyEntry> getPred() {
        return pred;
    }
}

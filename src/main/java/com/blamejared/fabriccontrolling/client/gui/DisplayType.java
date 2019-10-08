package com.blamejared.fabriccontrolling.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;

import java.util.function.Predicate;

public enum DisplayType {
    ALL(keyEntry -> true), CONFLICTS(keyEntry -> {
        if(keyEntry.binding.isNotBound()) {
            return false;
        }
        for(KeyBinding keyBinding : MinecraftClient.getInstance().options.keysAll) {
            if(!keyBinding.getId().equals(keyEntry.binding.getId())) {
                if(keyBinding.getName().equals(keyEntry.binding.getName())) {
                    return true;
                }
            }
        }
        return false;
    }), UNBOUND(keyEntry -> {
        return keyEntry.binding.isNotBound();
    });
    
    private Predicate<KeyBindingListWidgetNew.KeyEntry> pred;
    
    
    DisplayType(Predicate<KeyBindingListWidgetNew.KeyEntry> pred) {
        this.pred = pred;
    }
    
    
    public Predicate<KeyBindingListWidgetNew.KeyEntry> getPred() {
        return pred;
    }
}

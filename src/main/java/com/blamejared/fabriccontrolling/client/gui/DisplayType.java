package com.blamejared.fabriccontrolling.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.settings.KeyBinding;

import java.util.function.Predicate;

public enum DisplayType {
    ALL(keyEntry -> true), CONFLICTS(keyEntry -> {
        if(keyEntry.keyBinding.isUnbound()){
            return false;
        }
        for(KeyBinding keyBinding : MinecraftClient.getInstance().options.keysAll) {
            if(!keyBinding.method_1431().equalsIgnoreCase(keyEntry.keyBinding.method_1431())){
                if(keyBinding.getName().equals(keyEntry.keyBinding.getName())){
                    return true;
                }
            }
        }
        return false;
    }), UNBOUND(keyEntry -> {
        return keyEntry.keyBinding.isUnbound();
    });
    
    private Predicate<KeyBindingListWidgetNew.KeyEntry> pred;
    
    
    DisplayType(Predicate<KeyBindingListWidgetNew.KeyEntry> pred) {
        this.pred = pred;
    }
    
    
    public Predicate<KeyBindingListWidgetNew.KeyEntry> getPred() {
        return pred;
    }
}

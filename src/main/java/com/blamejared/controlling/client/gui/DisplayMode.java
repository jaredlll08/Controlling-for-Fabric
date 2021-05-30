package com.blamejared.controlling.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.Objects;
import java.util.function.Predicate;

public enum DisplayMode {
    ALL(key -> true), NONE(KeyBinding::isUnbound), CONFLICTING(key -> {
        for(KeyBinding k : MinecraftClient.getInstance().options.keysAll) {
            if(!Objects.equals(key.getTranslationKey(), k.getTranslationKey()) && !k.isUnbound()) {
                // this is not actually "equals", it's more like "conflicts"
                if(k.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    });
    
    private final Predicate<KeyBinding> filter;
    
    
    DisplayMode(Predicate<KeyBinding> filter) {
        this.filter = filter;
    }
    
    
    public Predicate<KeyBinding> getFilter() {
        return filter;
    }
}

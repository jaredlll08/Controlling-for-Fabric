package com.blamejared.controlling.mixin;

import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(InputUtil.Key.class)
public interface KeyAccessor {
    @Accessor("KEYS")
    static Map<String, InputUtil.Key> getKeys() {
        throw new IllegalStateException();
    }
}

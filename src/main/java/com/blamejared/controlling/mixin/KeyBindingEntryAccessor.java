package com.blamejared.controlling.mixin;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public interface KeyBindingEntryAccessor {
    @Accessor("binding")
    KeyBinding binding();

    @Accessor("bindingName")
    Text bindingName();
}

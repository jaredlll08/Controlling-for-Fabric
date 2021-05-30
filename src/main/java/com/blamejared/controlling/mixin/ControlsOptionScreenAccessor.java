package com.blamejared.controlling.mixin;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlsOptionsScreen.class)
public interface ControlsOptionScreenAccessor {
    @Accessor
    void setKeyBindingListWidget(ControlsListWidget widget);
    @Accessor
    ControlsListWidget getKeyBindingListWidget();
    @Accessor
    void setResetButton(ButtonWidget resetButton);
    @Accessor
    ButtonWidget getResetButton();
}

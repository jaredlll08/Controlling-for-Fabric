package com.blamejared.controlling.mixin;

import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlsListWidget.CategoryEntry.class)
public interface CategoryEntryAccessor {
    @Accessor("text")
    Text text();
}

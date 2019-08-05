package com.blamejared.fabriccontrolling.mixin;

import com.blamejared.fabriccontrolling.client.gui.ControlsSettingsGuiNew;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.options.ControlsOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class OpenGuiMixin {
    
    @Shadow
    public Screen currentScreen;

    @ModifyVariable(method = "openScreen", at = @At("HEAD"), argsOnly = true)
    private Screen upgradeControlScreen(Screen opened) {
        // Swap the control options screen with our own instance whenever something tries to open one
        if(opened != null && ControlsOptionsScreen.class.equals(opened.getClass())) {
            return new ControlsSettingsGuiNew(this.currentScreen, MinecraftClient.getInstance().options);
        }
        return opened;
    }
}

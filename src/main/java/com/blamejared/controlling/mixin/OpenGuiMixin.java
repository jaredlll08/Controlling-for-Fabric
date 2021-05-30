package com.blamejared.controlling.mixin;

import com.blamejared.controlling.client.gui.ControllingOptionsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
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
            return new ControllingOptionsScreen(this.currentScreen, MinecraftClient.getInstance().options);
        }
        return opened;
    }
}

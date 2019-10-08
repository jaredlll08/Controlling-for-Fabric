package com.blamejared.fabriccontrolling.mixin;

import com.blamejared.fabriccontrolling.client.gui.ControlsSettingsGuiNew;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class OpenGuiMixin {
    
    @Shadow
    public Screen currentScreen;

    @Inject(method = "openScreen", at = @At("HEAD"))
    private void dummyGenerateRefmap(Screen screen, CallbackInfo ci) {
        // NO-OP this injection is only here to generate the refmap
    }

    @ModifyVariable(method = "openScreen", at = @At("HEAD"), argsOnly = true)
    private Screen upgradeControlScreen(Screen opened) {
        // Swap the control options screen with our own instance whenever something tries to open one
        if(opened != null && ControlsOptionsScreen.class.equals(opened.getClass())) {
            return new ControlsSettingsGuiNew(this.currentScreen, MinecraftClient.getInstance().options);
        }
        return opened;
    }
}

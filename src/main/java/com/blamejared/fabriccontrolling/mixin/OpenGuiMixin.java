package com.blamejared.fabriccontrolling.mixin;

import com.blamejared.fabriccontrolling.client.gui.ControlsSettingsGuiNew;
import net.minecraft.client.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.ingame.DeathScreen;
import net.minecraft.client.gui.menu.MultiplayerScreen;
import net.minecraft.client.gui.menu.options.ControlsOptionsScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.*;
import net.minecraft.client.util.*;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.*;

@Mixin(MinecraftClient.class)
public class OpenGuiMixin {
    
    @Shadow
    public Screen currentScreen;
    @Shadow
    public ClientWorld world;
    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    public GameOptions options;
    @Shadow
    public InGameHud inGameHud;
    @Shadow
    public Mouse mouse;
    @Shadow
    public Window window;
    @Shadow
    public boolean skipGameRender;
    @Shadow
    private SoundManager soundManager;
    
    /**
     * @author
     */
    @Overwrite
    public void openScreen(Screen screen_1) {
        if(this.currentScreen != null) {
            this.currentScreen.removed();
        }
        
        if(screen_1 == null && this.world == null) {
            screen_1 = new MainMenuScreen();
        } else if(screen_1 == null && this.player.getHealth() <= 0.0F) {
            screen_1 = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
        }
        
        if(screen_1 instanceof MainMenuScreen || screen_1 instanceof MultiplayerScreen) {
            this.options.debugEnabled = false;
            this.inGameHud.getChatHud().clear(true);
        }
        
        if(screen_1 != null && ControlsOptionsScreen.class.equals(screen_1.getClass())) {
            screen_1 = new ControlsSettingsGuiNew(this.currentScreen, MinecraftClient.getInstance().options);
        }
        
        this.currentScreen = screen_1;
        if(screen_1 != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            screen_1.init(MinecraftClient.getInstance(), this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
            NarratorManager.INSTANCE.method_19788(screen_1.getNarrationMessage());
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
        
    }
    
    
}

package com.blamejared.fabriccontrolling.mixin;

import com.blamejared.fabriccontrolling.client.gui.ControlsSettingsGuiNew;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.ingame.DeathGui;
import net.minecraft.client.gui.menu.MultiplayerGui;
import net.minecraft.client.gui.menu.settings.ControlsSettingsGui;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.settings.*;
import net.minecraft.client.util.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.*;

@Mixin(MinecraftClient.class)
public class OpenGuiMixin {
    
    @Shadow
    public Gui currentGui;
    
    @Shadow
    public ClientWorld world;
    
    @Shadow
    public ClientPlayerEntity player;
    
    @Shadow
    public GameOptions options;
    
    @Shadow
    public InGameHud hudInGame;
    
    @Shadow
    public Mouse mouse;
    
    @Shadow
    public boolean field_1743;
    
    @Shadow
    public Window window;
    
    @Shadow
    private SoundLoader soundLoader;
    
    /**
     * @author Jaredlll08
     */
    @Overwrite
    public void openGui(Gui gui) {
        if(this.currentGui != null) {
            this.currentGui.onClosed();
        }
        
        if(gui == null && this.world == null) {
            gui = new MainMenuGui();
        } else if(gui == null && this.player.getHealth() <= 0.0F) {
            gui = new DeathGui((TextComponent) null);
        }
        
        if(gui instanceof MainMenuGui || gui instanceof MultiplayerGui) {
            this.options.debugEnabled = false;
            this.hudInGame.getHudChat().clear(true);
        }
        
        if(gui != null && ControlsSettingsGui.class.equals(gui.getClass())) {
            gui = new ControlsSettingsGuiNew(this.currentGui, MinecraftClient.getInstance().options);
        }
        this.currentGui = (Gui) gui;
        if(gui != null) {
            this.mouse.method_1610();
            KeyBinding.method_1437();
            ((Gui) gui).initialize(MinecraftClient.getInstance(), this.window.getScaledWidth(), this.window.getScaledHeight());
            this.field_1743 = false;
        } else {
            this.soundLoader.resume();
            this.mouse.method_1612();
        }
        //        if(gui.getClass().equals(ControlsSettingsGui.class)) {
        //            ControlsSettingsGui cGui = (ControlsSettingsGui) gui;
        //            try {
        //                Field parent = cGui.getClass().getDeclaredField("parent");
        //                parent.setAccessible(true);
        //                Object o = parent.get(cGui);
        //                MinecraftClient.getInstance().openGui(new ControlsSettingsGuiNew((Gui) o, MinecraftClient.getInstance().settings));
        //            } catch(NoSuchFieldException | IllegalAccessException e) {
        //                e.printStackTrace();
        //            }
        //
        //        } else {
        //        }
    }
    
    
}

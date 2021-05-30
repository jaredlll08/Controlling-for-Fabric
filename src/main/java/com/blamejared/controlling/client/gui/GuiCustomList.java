package com.blamejared.controlling.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;

import java.util.List;

public class GuiCustomList extends ControlsListWidget {
    
    public List<Entry> allEntries;
    
    public GuiCustomList(ControlsOptionsScreen controls, MinecraftClient mcIn) {
        super(controls, mcIn);
    }
    
    public List<Entry> getAllEntries() {
        
        return allEntries;
    }
    
    public void add(Entry ent) {
        this.children().add(ent);
        allEntries.add(ent);
    }
}

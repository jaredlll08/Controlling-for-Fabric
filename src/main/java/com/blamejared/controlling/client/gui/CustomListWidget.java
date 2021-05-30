package com.blamejared.controlling.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CustomListWidget extends ControlsListWidget {

    private @Nullable List<Entry> allEntries;
    
    public CustomListWidget(ControlsOptionsScreen controls, MinecraftClient mcIn) {
        super(controls, mcIn);
    }
    
    public List<Entry> getAllEntries() {
        // abusing lazy loading to actually create the list in the super constructor 😎
        if (allEntries == null) allEntries = new ArrayList<>();
        return allEntries;
    }

    @Override
    protected int addEntry(Entry entry) {
        this.getAllEntries().add(entry);
        return super.addEntry(entry);
    }
}

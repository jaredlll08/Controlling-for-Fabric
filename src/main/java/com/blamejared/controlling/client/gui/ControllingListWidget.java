package com.blamejared.controlling.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ControllingListWidget extends ControlsListWidget {
    
    private List<Entry> allListeners;
    
    public ControllingListWidget(ControlsSettingsGuiNew parent, MinecraftClient mc) {
        super(parent, mc);
        this.bottom = parent.height - 80;
    }

    @Override
    protected int addEntry(ControlsListWidget.Entry entry) {
        this.getAllListeners().add(entry);
        return super.addEntry(entry);
    }

    public List<Entry> getAllListeners() {
        if (this.allListeners == null) this.allListeners = new ArrayList<>();
        return allListeners;
    }
}

package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.mixin.KeyBindingEntryAccessor;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Comparator;
import java.util.List;

public enum SortOrder {
    NONE((o1, o2) -> 0),
    AZ(Comparator.comparing(o -> I18n.translate(((KeyBindingEntryAccessor) o).binding().getTranslationKey()))),
    ZA(AZ.sorter.reversed());
    
    private final Comparator<ControlsListWidget.Entry> sorter;
    
    SortOrder(Comparator<ControlsListWidget.Entry> sorter) {
        this.sorter = sorter;
    }
    
    public SortOrder cycle() {
        return SortOrder.values()[(this.ordinal() + 1) % SortOrder.values().length];
    }
    
    public void sort(List<ControlsListWidget.Entry> list) {
        list.sort(this.sorter);
    }

    public Text getName() {
        return switch (this) {
            case NONE -> new TranslatableText("options.sortNone");
            case AZ -> new TranslatableText("options.sortAZ");
            case ZA -> new TranslatableText("options.sortZA");
        };
    }
}

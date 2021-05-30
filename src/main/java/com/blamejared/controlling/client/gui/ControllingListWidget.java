package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.mixin.CategoryEntryAccessor;
import com.blamejared.controlling.mixin.KeyBindingEntryAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;

import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ControllingListWidget extends CustomListWidget {
    
    public ControllingListWidget(ControllingOptionsScreen parent, MinecraftClient mc) {
        super(parent, mc);
        this.bottom = parent.height - 80;
    }

    void filterKeys(String lastSearch, DisplayMode displayMode, SearchType searchType, SortOrder sortOrder) {
        this.children().clear();
        if(lastSearch.isEmpty() && displayMode == DisplayMode.ALL && sortOrder == SortOrder.NONE) {
            this.children().addAll(this.getAllEntries());
            return;
        }

        Predicate<KeyBinding> filters = displayMode.getFilter();
        if(!lastSearch.isEmpty()) {
            filters = switch (searchType) {
                case NAME -> filters.and(key -> I18n.translate(key.getTranslationKey()).toLowerCase().contains(lastSearch.toLowerCase()));
                case KEY -> filters.and(key -> I18n.translate(key.getBoundKeyTranslationKey()).toLowerCase().contains(lastSearch.toLowerCase()));
                case CATEGORY -> filters.and(key -> I18n.translate(key.getCategory()).toLowerCase().contains(lastSearch.toLowerCase()));
            };
        }

        for(Entry entry : this.getAllEntries()) {
            if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
                if(!(entry instanceof KeyBindingEntryAccessor keyEntry) || filters.test(keyEntry.binding())) {
                    this.children().add(entry);
                }
            } else if (entry instanceof KeyBindingEntryAccessor keyEntry && filters.test(keyEntry.binding())) {
                this.children().add(entry);
            }
        }

        if(searchType == SearchType.CATEGORY && sortOrder == SortOrder.NONE && displayMode == DisplayMode.ALL) {
            this.children().removeIf(entry -> {
                if(entry instanceof CategoryEntry centry) {
                    for(Entry child : this.children()) {
                        if(child instanceof KeyBindingEntry childEntry) {
                            if(new TranslatableText(((KeyBindingEntryAccessor) childEntry).binding().getCategory()).equals(((CategoryEntryAccessor) centry).text())) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
                return false;
            });
        }

        sortOrder.sort(this.children());
    }
}

package com.blamejared.fabriccontrolling.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.TextFormat;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class KeyBindingListWidgetNew extends EntryListWidget<KeyBindingListWidgetNew.Entry> {
    
    private final ControlsSettingsGuiNew gui;
    private final MinecraftClient client;
    private int maxWidth;
    private List<Entry> allListeners;
    
    public KeyBindingListWidgetNew(ControlsSettingsGuiNew parent, MinecraftClient mc) {
        super(mc, parent.width + 45, parent.height, 43, parent.height - 80, 20);
        this.gui = parent;
        this.client = mc;
        allListeners = new ArrayList<>();
        KeyBinding[] keys = ArrayUtils.clone(mc.options.keysAll);
        Arrays.sort(keys);
        String var4 = null;
        
        for(int i = 0; i < keys.length; ++i) {
            KeyBinding var8 = keys[i];
            String var9 = var8.getCategory();
            if(!var9.equals(var4)) {
                var4 = var9;
                this.addListener(new CategoryEntry(var9));
            }
            
            int var10 = mc.textRenderer.getStringWidth(I18n.translate(var8.getId()));
            if(var10 > this.maxWidth) {
                this.maxWidth = var10;
            }
            
            this.addListener(new KeyEntry(var8));
        }
        
    }
    
    
    protected final void addListener(Entry ent) {
        children().add(ent);
        getAllListeners().add(ent);
    }
    
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }
    
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }
    
    public List<Entry> getAllListeners() {
        return allListeners;
    }
    
    public void setAllListeners(List<Entry> allListeners) {
        this.allListeners = allListeners;
    }
    
    @Environment(EnvType.CLIENT)
    public class KeyEntry extends KeyBindingListWidgetNew.Entry {
        
        public final KeyBinding binding;
        public final String bindingName;
        public final ButtonWidget editButton;
        private final ButtonWidget resetButton;
        
        private KeyEntry(final KeyBinding keyBinding_1) {
            this.binding = keyBinding_1;
            this.bindingName = I18n.translate(keyBinding_1.getId());
            this.editButton = new ButtonWidget(0, 0, 75, 20, this.bindingName, (buttonWidget_1) -> KeyBindingListWidgetNew.this.gui.focusedBinding = keyBinding_1) {
                protected String getNarrationMessage() {
                    return keyBinding_1.isNotBound() ? I18n.translate("narrator.controls.unbound", KeyEntry.this.bindingName) : I18n.translate("narrator.controls.bound", KeyEntry.this.bindingName, super.getNarrationMessage());
                }
            };
            
            this.resetButton = new ButtonWidget(0, 0, 50, 20, I18n.translate("controls.reset"), (buttonWidget_1) -> {
                KeyBindingListWidgetNew.this.minecraft.options.setKeyCode(keyBinding_1, keyBinding_1.getDefaultKeyCode());
                KeyBinding.updateKeysByCode();
            }) {
                protected String getNarrationMessage() {
                    return I18n.translate("narrator.controls.reset", KeyEntry.this.bindingName);
                }
            };
        }
        
        @Override
        public void render(int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, boolean boolean_1, float float_1) {
            boolean boolean_2 = KeyBindingListWidgetNew.this.gui.focusedBinding == this.binding;
            TextRenderer var10000 = KeyBindingListWidgetNew.this.minecraft.textRenderer;
            String var10001 = this.bindingName;
            float var10002 = (float) (int_3 + 90 - KeyBindingListWidgetNew.this.maxWidth);
            int var10003 = int_2 + int_5 / 2;
            KeyBindingListWidgetNew.this.minecraft.textRenderer.getClass();
            var10000.draw(var10001, var10002, (float) (var10003 - 9 / 2), 16777215);
            if(!this.binding.isDefault()) {
                this.resetButton.x = int_3 + 190;
                this.resetButton.y = int_2;
                this.resetButton.active = !this.binding.isDefault();
                this.resetButton.render(int_6, int_7, float_1);
            }
            this.editButton.x = int_3 + 105;
            this.editButton.y = int_2;
            this.editButton.setMessage(this.binding.getLocalizedName());
            boolean boolean_3 = false;
            if(!this.binding.isNotBound()) {
                KeyBinding[] var12 = KeyBindingListWidgetNew.this.minecraft.options.keysAll;
                int var13 = var12.length;
                
                for(int var14 = 0; var14 < var13; ++var14) {
                    KeyBinding keyBinding_1 = var12[var14];
                    if(keyBinding_1 != this.binding && this.binding.equals(keyBinding_1)) {
                        boolean_3 = true;
                        break;
                    }
                }
            }
            
            if(boolean_2) {
                this.editButton.setMessage(TextFormat.WHITE + "> " + TextFormat.YELLOW + this.editButton.getMessage() + TextFormat.WHITE + " <");
            } else if(boolean_3) {
                this.editButton.setMessage(TextFormat.RED + this.editButton.getMessage());
            }
            
            this.editButton.render(int_6, int_7, float_1);
        }
        
        
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            if(this.editButton.mouseClicked(double_1, double_2, int_1)) {
                return true;
            } else {
                return this.resetButton.mouseClicked(double_1, double_2, int_1);
            }
        }
        
        public boolean mouseReleased(double double_1, double double_2, int int_1) {
            return this.editButton.mouseReleased(double_1, double_2, int_1) || this.resetButton.mouseReleased(double_1, double_2, int_1);
        }
        
        KeyEntry(KeyBinding var2, Object var3) {
            this(var2);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("KeyEntry{");
            sb.append("binding=").append(binding);
            sb.append(", bindingName='").append(bindingName).append('\'');
            sb.append('}');
            return sb.toString();
        }
        
        
    }
    
    @Environment(EnvType.CLIENT)
    public class CategoryEntry extends KeyBindingListWidgetNew.Entry {
        
        private final String name;
        private final int width;
        
        public CategoryEntry(String name) {
            this.name = I18n.translate(name);
            this.width = KeyBindingListWidgetNew.this.client.textRenderer.getStringWidth(this.name);
        }
        
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CategoryEntry{");
            sb.append("name='").append(name).append('\'');
            sb.append(", width=").append(width);
            sb.append('}');
            return sb.toString();
        }
        
        @Override
        public void render(int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, boolean boolean_1, float float_1) {
            
            TextRenderer var10000 = KeyBindingListWidgetNew.this.minecraft.textRenderer;
            String var10001 = this.name;
            float var10002 = (float) (KeyBindingListWidgetNew.this.minecraft.currentScreen.width / 2 - this.width / 2);
            int var10003 = int_2 + int_5;
            KeyBindingListWidgetNew.this.minecraft.textRenderer.getClass();
            var10000.draw(var10001, var10002, (float) (var10003 - 9 - 1), 16777215);
            
        }
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends EntryListWidget.Entry<KeyBindingListWidgetNew.Entry> {}
}

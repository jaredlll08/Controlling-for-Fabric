package com.blamejared.fabriccontrolling.client.gui;

import net.fabricmc.api.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextFormat;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

@Environment(EnvType.CLIENT)
public class KeyBindingListWidgetNew extends EntryListWidget<KeyBindingListWidgetNew.Entry> {
    
    private final ControlsSettingsGuiNew gui;
    private final MinecraftClient client;
    private int maxWidth;
    private List<Entry> allListeners;
    
    public KeyBindingListWidgetNew(ControlsSettingsGuiNew parent, MinecraftClient mc) {
        super(mc, parent.width + 45, parent.height, 63, parent.height - 80, 20);
        this.gui = parent;
        this.client = mc;
        allListeners = new ArrayList<>();
        KeyBinding[] keys = ArrayUtils.clone(mc.options.keysAll);
        Arrays.sort(keys);
        String var4 = null;
        
        for(int i = 0; i < keys.length; ++i) {
            KeyBinding var8 = keys[i];
            String var9 = var8.method_1423();
            if(!var9.equals(var4)) {
                var4 = var9;
                this.addListener(new CategoryEntry(var9));
            }
            
            int var10 = mc.fontRenderer.getStringWidth(I18n.translate(var8.method_1431()));
            if(var10 > this.maxWidth) {
                this.maxWidth = var10;
            }
            
            this.addListener(new KeyEntry(var8));
        }
        
    }
    
    
    protected final void addListener(Entry ent) {
        getListeners().add(ent);
        getAllListeners().add(ent);
    }
    
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 15;
    }
    
    public int getEntryWidth() {
        return super.getEntryWidth() + 32;
    }
    
    public List<Entry> getAllListeners() {
        return allListeners;
    }
    
    public void setAllListeners(List<Entry> allListeners) {
        this.allListeners = allListeners;
    }
    
    @Environment(EnvType.CLIENT)
    public class KeyEntry extends KeyBindingListWidgetNew.Entry {
        
        public final KeyBinding keyBinding;
        public final String keyDesc;
        public final ButtonWidget change;
        
        private KeyEntry(final KeyBinding var2) {
            this.keyBinding = var2;
            this.keyDesc = I18n.translate(var2.method_1431());
            this.change = new ButtonWidget(0, 0, 0, 75, 20, I18n.translate(var2.method_1431())) {
                public void onPressed(double var1, double var3) {
                    KeyBindingListWidgetNew.this.gui.field_2727 = var2;
                }
            };
        }
        
        public void drawEntry(int var1, int var2, int var3, int var4, boolean var5, float var6) {
            int var7 = this.method_1906();
            int var8 = this.method_1907();
            boolean var9 = KeyBindingListWidgetNew.this.gui.field_2727 == this.keyBinding;
            KeyBindingListWidgetNew.this.client.fontRenderer.draw(this.keyDesc, (float) (var8 + 90 - KeyBindingListWidgetNew.this.maxWidth), (float) (var7 + var2 / 2 - KeyBindingListWidgetNew.this.client.fontRenderer.FONT_HEIGHT / 2), 16777215);
            this.change.x = var8 + 105;
            this.change.y = var7;
            this.change.text = this.keyBinding.method_16007();
            boolean var10 = false;
            if(!this.keyBinding.isUnbound()) {
                KeyBinding[] var11 = KeyBindingListWidgetNew.this.client.options.keysAll;
                int var12 = var11.length;
                
                for(int var13 = 0; var13 < var12; ++var13) {
                    KeyBinding var14 = var11[var13];
                    if(var14 != this.keyBinding && this.keyBinding.equals(var14)) {
                        var10 = true;
                        break;
                    }
                }
            }
            
            if(var9) {
                this.change.text = TextFormat.WHITE + "> " + TextFormat.YELLOW + this.change.text + TextFormat.WHITE + " <";
            } else if(var10) {
                this.change.text = TextFormat.RED + this.change.text;
            }
            
            this.change.draw(var3, var4, var6);
        }
        
        public boolean mouseClicked(double var1, double var3, int var5) {
            if(this.change.mouseClicked(var1, var3, var5)) {
                return true;
            } else {
                return false;
            }
        }
        
        public boolean mouseReleased(double var1, double var3, int var5) {
            return this.change.mouseReleased(var1, var3, var5);
        }
        
        KeyEntry(KeyBinding var2, Object var3) {
            this(var2);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("KeyEntry{");
            sb.append("keyBinding=").append(keyBinding);
            sb.append(", keyDesc='").append(keyDesc).append('\'');
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
            this.width = KeyBindingListWidgetNew.this.client.fontRenderer.getStringWidth(this.name);
        }
        
        public void drawEntry(int var1, int var2, int var3, int var4, boolean var5, float var6) {
            KeyBindingListWidgetNew.this.client.fontRenderer.draw(this.name, (float) (KeyBindingListWidgetNew.this.client.currentGui.width / 2 - this.width / 2), (float) (this.method_1906() + var2 - KeyBindingListWidgetNew.this.client.fontRenderer.FONT_HEIGHT - 1), 16777215);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("CategoryEntry{");
            sb.append("name='").append(name).append('\'');
            sb.append(", width=").append(width);
            sb.append('}');
            return sb.toString();
        }
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends EntryListWidget.Entry<KeyBindingListWidgetNew.Entry> {}
}

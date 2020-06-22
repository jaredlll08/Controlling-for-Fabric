package com.blamejared.fabriccontrolling.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.options.KeyBinding;
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
            
            int var10 = mc.textRenderer.getWidth(I18n.translate(var8.getTranslationKey()));
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

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 15;
    }

    @Override
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
    public class KeyEntry extends Entry {
        
        public final KeyBinding binding;
        public final String bindingName;
        public final ButtonWidget editButton;
        private final ButtonWidget resetButton;
        
        private KeyEntry(final KeyBinding keyBinding_1) {
            this.binding = keyBinding_1;
            this.bindingName = I18n.translate(keyBinding_1.getTranslationKey());
            this.editButton = new ButtonWidget(0, 0, 75, 20, new LiteralText(this.bindingName), (buttonWidget_1) -> KeyBindingListWidgetNew.this.gui.focusedBinding = keyBinding_1) {
                protected MutableText getNarrationMessage() {
                    return keyBinding_1.isUnbound() ? new TranslatableText("narrator.controls.unbound", KeyEntry.this.bindingName) : new TranslatableText("narrator.controls.bound", KeyEntry.this.bindingName, super.getNarrationMessage());
                }
            };
            
            this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), (buttonWidget_1) -> {
                KeyBindingListWidgetNew.this.client.options.setKeyCode(keyBinding_1, keyBinding_1.getDefaultKey());
                KeyBinding.updateKeysByCode();
            }) {
                protected MutableText getNarrationMessage() {
                    return new TranslatableText("narrator.controls.reset", KeyEntry.this.bindingName);
                }
            };
        }
        
        @Override
        public void render(MatrixStack matrices, int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, boolean boolean_1, float float_1) {
            boolean boolean_2 = KeyBindingListWidgetNew.this.gui.focusedBinding == this.binding;
            KeyBindingListWidgetNew.this.client.textRenderer.draw(matrices, this.bindingName, (float) (int_3 + 90 - KeyBindingListWidgetNew.this.maxWidth), (float) (int_2 + int_5 / 2 - 9 / 2), 0xffffff);
            if(!this.binding.isDefault()) {
                this.resetButton.x = int_3 + 190;
                this.resetButton.y = int_2;
                this.resetButton.active = !this.binding.isDefault();
                this.resetButton.render(matrices, int_6, int_7, float_1);
            }
            this.editButton.x = int_3 + 105;
            this.editButton.y = int_2;
            this.editButton.setMessage(this.binding.getBoundKeyLocalizedText());
            boolean boolean_3 = false;
            if(!this.binding.isUnbound()) {
                KeyBinding[] var12 = KeyBindingListWidgetNew.this.client.options.keysAll;

                for (KeyBinding keyBinding_1 : var12) {
                    if (keyBinding_1 != this.binding && this.binding.equals(keyBinding_1)) {
                        boolean_3 = true;
                        break;
                    }
                }
            }

            if (boolean_2) {
                this.editButton.setMessage(
                        new LiteralText("> ").formatted(Formatting.WHITE)
                                .append(((MutableText) this.editButton.getMessage()).formatted(Formatting.YELLOW))
                                .append(new LiteralText(" <").formatted(Formatting.WHITE))
                );
            } else if (boolean_3) {
                this.editButton.setMessage(((MutableText) this.editButton.getMessage()).formatted(Formatting.RED));
            }
            
            this.editButton.render(matrices, int_6, int_7, float_1);
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
    public class CategoryEntry extends Entry {
        
        private final String name;
        private final int width;
        
        public CategoryEntry(String name) {
            this.name = I18n.translate(name);
            this.width = KeyBindingListWidgetNew.this.client.textRenderer.getWidth(this.name);
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
        public void render(MatrixStack matrices, int int_1, int int_2, int int_3, int int_4, int int_5, int int_6, int int_7, boolean boolean_1, float float_1) {
            KeyBindingListWidgetNew.this.client.textRenderer.draw(
                    matrices,
                    this.name,
                    (float) (KeyBindingListWidgetNew.this.client.currentScreen.width / 2 - this.width / 2),
                    (float) ((int_2 + int_5) - 9 - 1),
                    0xffffff);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends EntryListWidget.Entry<Entry> {}
}

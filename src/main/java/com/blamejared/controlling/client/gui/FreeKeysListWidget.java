package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.mixin.KeyAccessor;
import com.blamejared.controlling.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FreeKeysListWidget extends CustomListWidget {
    
    private final ControlsOptionsScreen controlsScreen;
    private final MinecraftClient mc;
    private int maxListLabelWidth;
    
    List<KeyBinding> keyBindings;
    
    public FreeKeysListWidget(ControlsOptionsScreen controls, MinecraftClient mcIn) {
        super(controls, mcIn);
        this.width = controls.width + 45;
        this.height = controls.height;
        this.top = 43;
        this.bottom = controls.height - 80;
        this.right = controls.width + 45;
        this.controlsScreen = controls;
        this.mc = mcIn;
        children().clear();
        keyBindings = Arrays.stream(mc.options.keysAll).collect(Collectors.toList());
        
        recalculate();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void recalculate() {
        
        this.children().clear();
        this.getAllEntries().clear();
        
        this.addEntry(new HeaderEntry("Available Keys"));
        KeyAccessor.getKeys().values().stream().filter(input -> {
            return !input.toString().startsWith("key.keyboard.world");
        }).sorted(Comparator.comparing(o -> o.getLocalizedText().getString())).forEach(input -> {
            if(keyBindings.stream().noneMatch(keyBinding -> ((KeyBindingAccessor) keyBinding).getBoundKey().equals(input))) {
                int i = mc.textRenderer.getWidth(input.getLocalizedText());
                if(i > this.maxListLabelWidth) {
                    this.maxListLabelWidth = i;
                }
                this.addEntry(new InputEntry(input));
            }
        });
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 15 + 20;
    }
    
    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 32;
    }

    void filterKeys(String lastSearch) {
        this.children().clear();
        if(lastSearch.isEmpty()) {
            this.children().addAll(this.getAllEntries());
            return;
        }

        this.setScrollAmount(0);

        for(Entry entry : getAllEntries()) {
            if(entry instanceof InputEntry inputEntry) {
                if(inputEntry.input.toString().toLowerCase().contains(lastSearch.toLowerCase())) {
                    this.children().add(entry);
                }
            } else {
                this.children().add(entry);
            }

        }
    }

    public class InputEntry extends Entry {
        
        private final InputUtil.Key input;
        
        public InputEntry(InputUtil.Key input) {
            this.input = input;
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }

        @Override
        public List<? extends Selectable> method_37025() {
            return List.of();
        }

        public InputUtil.Key getInput() {
            return input;
        }
    
        @Override
        public void render(MatrixStack stack, int slotIndex, int y, int x, int p_render_4_, int p_render_5_, int mouseX, int mouseY, boolean p_render_8_, float p_render_9_) {
            String str = this.input.toString() + " - " + input.getCode();// + " - " + input.func_237520_d_().getString() + " - " + input.getKeyCode();
            int length = mc.textRenderer.getWidth(input.getLocalizedText());
            
            FreeKeysListWidget.this.mc.textRenderer.draw(stack, str, x, (float) (y + p_render_5_ / 2 - 9 / 2), 0xffffff);
            FreeKeysListWidget.this.controlsScreen.renderTooltip(stack, Collections.singletonList(input.getLocalizedText()), x + p_render_4_ - (length), y + p_render_5_);
        }
        
    }
    
    public class HeaderEntry extends Entry {
        
        private final String text;
        
        public HeaderEntry(String text) {
            
            this.text = text;
        }
        
        @Override
        public List<? extends Element> children() {
            return List.of();
        }
        
        @Override
        public void render(MatrixStack stack, int slotIndex, int y, int x, int p_render_4_, int p_render_5_, int mouseX, int mouseY, boolean p_render_8_, float p_render_9_) {
            drawTextWithShadow(stack, mc.textRenderer, new TranslatableText("options.availableKeys"),  (controlsScreen.width / 2 - this.text.length() / 2), (y + p_render_5_ - 9 - 1), 0xffffff);
        }

        @Override
        public List<? extends Selectable> method_37025() {
            return List.of();
        }
    }
    
}
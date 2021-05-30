package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.mixin.KeyBindingEntryAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import java.util.Iterator;
import java.util.function.Predicate;

@SuppressWarnings("ALL")
@Environment(EnvType.CLIENT)
public class ControlsSettingsGuiNew extends ControlsOptionsScreen {
    
    private final Screen parent;
    private final GameOptions options;
    private ControllingListWidget keyBindingListWidget;
    private ButtonWidget resetButton;
    
    
    private TextFieldWidget search;
    private String lastSearch;
    private DisplayMode displayMode;
    public ButtonWidget noneBtn;
    public ButtonWidget conflictBtn;
    public SearchType searchType;
    public ButtonWidget sTypeBtn;
    
    public ControlsSettingsGuiNew(Screen var1, GameOptions var2) {
        super(var1, var2);
        this.parent = var1;
        this.options = var2;
    }

    @Override
    protected void init() {
        this.lastSearch = "";
        displayMode = DisplayMode.ALL;
        searchType = SearchType.NAME;
        this.search = new TextFieldWidget(textRenderer, this.width / 2 - 154, this.height - 29 - 23, 148, 18, new LiteralText(""));
        this.keyBindingListWidget = new ControllingListWidget(this, this.client);
        this.addSelectableChild(this.keyBindingListWidget);
        this.setFocused(this.keyBindingListWidget);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 18, 150, 20, new TranslatableText("options.mouse_settings"), (buttonWidget) -> {
            this.client.openScreen(new MouseOptionsScreen(this, this.gameOptions));
        }));
        this.addDrawableChild(Option.AUTO_JUMP.createButton(this.gameOptions, this.width / 2 - 155 + 160, 18, 150));
        
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, new TranslatableText("gui.done"), var1 -> ControlsSettingsGuiNew.this.client.openScreen(ControlsSettingsGuiNew.this.parent)));
        conflictBtn = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, new TranslatableText("options.showConflicts"), var1 -> {
            if(displayMode == DisplayMode.CONFLICTS) {
                conflictBtn.setMessage(new TranslatableText("options.showConflicts"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTS;
                conflictBtn.setMessage(new TranslatableText("options.showAll"));
                noneBtn.setMessage(new TranslatableText("options.showNone"));
            }
            filterKeys();
        }));
        noneBtn = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20, new TranslatableText("options.showNone"), var1 -> {
            if(displayMode == DisplayMode.NONE) {
                noneBtn.setMessage(new TranslatableText("options.showNone"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.NONE;
                noneBtn.setMessage(new TranslatableText("options.showAll"));
                conflictBtn.setMessage(new TranslatableText("options.showConflicts"));
            }
            filterKeys();
        }));
        sTypeBtn = this.addDrawableChild(new ButtonWidget(this.width / 2 - (155 / 2) + 20, this.height - 29 - 39 - (5), 53, 20, new TranslatableText(searchType.niceName()), var1 -> {
            searchType = searchType.cycle();
            var1.setMessage(new TranslatableText(searchType.niceName()));
            filterKeys();
        }));
        this.resetButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), var1 -> {
            KeyBinding[] var2 = this.client.options.keysAll;
            int var3 = var2.length;
            
            for(int var4 = 0; var4 < var3; ++var4) {
                KeyBinding keyBinding_1 = var2[var4];
                keyBinding_1.setBoundKey(keyBinding_1.getDefaultKey());
            }
            
            KeyBinding.updateKeysByCode();
            filterKeys();
        }));
        //        this.title = new TranslatableTextComponent("controls.title");;
        int index = 0;
        
        //        for(KeyBinding var5 : SETTINGS) {
        //            if(var5.isSlider()) {
        //                this.addButton(new OptionSliderWidget(var5.getId(), this.width / 2 - 155 + index % 2 * 160, 18 + 24 * (index >> 1), var5));
        //            } else {
        //                this.addButton(new OptionButtonWidget(var5.getId(), this.width / 2 - 155 + index % 2 * 160, 18 + 24 * (index >> 1), var5, this.options.getTranslatedName(var5)) {
        //                    public void onPressed(double var1, double var3) {
        //                        ControlsSettingsGuiNew.this.options.updateOption(this.getOption(), 1);
        //                        this.text = ControlsSettingsGuiNew.this.options.getTranslatedName(KeyBinding.byId(this.id));
        //                    }
        //                });
        //            }
        //            ++index;
        //        }
    }
    

    @Override
    public void render(MatrixStack matrices, int mx, int my, float pt) {
        
        this.renderBackground(matrices);
        this.keyBindingListWidget.render(matrices, mx, my, pt);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        boolean changed = false;
        KeyBinding[] keys = this.options.keysAll;
        
        for(KeyBinding var8 : keys) {
            if(!var8.isDefault()) {
                changed = true;
                break;
            }
        }
        
        this.resetButton.active = changed;
        search.render(matrices, mx, my, pt);
        superDraw(matrices, mx, my, pt);
        drawCenteredText(matrices, textRenderer, new TranslatableText("options.search"), this.width / 2 - (155 / 2), this.height - 29 - 39, 16777215);
    }
    
    
    @Override
    public void tick() {
        super.tick();
        search.tick();
        if(!lastSearch.equals(search.getText())) {
            filterKeys();
        }
    }
    
    public void filterKeys() {
        lastSearch = search.getText();
        keyBindingListWidget.children().clear();
        if(displayMode == DisplayMode.ALL && lastSearch.isEmpty()) {
            keyBindingListWidget.children().addAll(keyBindingListWidget.getAllListeners());
            return;
        }
        Predicate<ControlsListWidget.KeyBindingEntry> predicate = displayMode.getPred();
        if(!lastSearch.isEmpty()) {
            switch(searchType) {
                case NAME:
                    predicate = predicate.and(keyEntry -> ((KeyBindingEntryAccessor) keyEntry).bindingName().getString().toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case KEY:
                    predicate = predicate.and(keyEntry -> I18n.translate(((KeyBindingEntryAccessor) keyEntry).binding().getTranslationKey()).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case CATEGORY:
                    predicate = predicate.and(keyEntry -> I18n.translate(((KeyBindingEntryAccessor) keyEntry).binding().getCategory()).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
            }
        }
        
        for(ControllingListWidget.Entry entry : keyBindingListWidget.getAllListeners()) {
            if (entry instanceof ControlsListWidget.KeyBindingEntry ent && predicate.test(ent)) {
                keyBindingListWidget.children().add(ent);
            }
        }
        // Prevent scroll overflow (the setter auto clamps)
        this.keyBindingListWidget.setScrollAmount(this.keyBindingListWidget.getScrollAmount());
        
    }
    
    @Override
    public boolean charTyped(char var1, int var2) {
        return search.charTyped(var1, var2);
    }
    
    public boolean mouseClicked(double mx, double my, int btn) {
        
        boolean valid;
        if(this.focusedBinding != null) {
            if(search.isFocused()) {
                search.changeFocus(false);
            }
            this.options.setKeyCode(this.focusedBinding, InputUtil.Type.MOUSE.createFromCode(btn));
            this.focusedBinding = null;
            KeyBinding.updateKeysByCode();
            valid = true;
        } else if(btn == 0 && this.keyBindingListWidget.mouseClicked(mx, my, btn)) {
            if(search.isFocused()) {
                search.changeFocus(false);
            }
            this.setDragging(true);
            this.setFocused(this.keyBindingListWidget);
            valid = true;
        } else {
            valid = search.mouseClicked(mx, my, btn);
            if(!valid && search.isFocused() && btn == 1) {
                search.setText("");
                valid = true;
            }
        }
        
        if(!valid) {
            valid = superMouseClicked(mx, my, btn);
        }
        return valid;
    }
    
    
    public boolean mouseReleased(double mx, double my, int btn) {
        if(btn == 0 && this.keyBindingListWidget.mouseReleased(mx, my, btn)) {
            this.setDragging(false);
            return true;
        } else if(search.isFocused()) {
            return search.mouseReleased(mx, my, btn);
        } else {
            return superMouseReleased(mx, my, btn);
        }
    }
    
    
    public boolean keyPressed(int var1, int var2, int var3) {
        if(this.focusedBinding == null && search.keyPressed(var1, var2, var3)) {
            return true;
        }
        if(this.focusedBinding != null) {
            if(var1 == 256) {
                this.options.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEY);
            } else {
                this.options.setKeyCode(this.focusedBinding, InputUtil.fromKeyCode(var1, var2));
            }
            
            this.focusedBinding = null;
            this.time = Util.getMeasuringTimeMs();
            KeyBinding.updateKeysByCode();
            return true;
        } else {
            return superKeyPressed(var1, var2, var3);
        }
    }
    
    public boolean superKeyPressed(int var1, int var2, int var3) {
        if(var1 == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else {
            return super.keyPressed(var1, var2, var3);
        }
    }
    
    
    public boolean superMouseClicked(double var1, double var3, int var5) {
        Iterator var6 = this.children().iterator();
        
        Element var7;
        boolean var8;
        do
        {
            if(!var6.hasNext()) {
                return false;
            }
            
            var7 = (Element) var6.next();
            var8 = var7.mouseClicked(var1, var3, var5);
        } while(!var8);
        
        this.focusOn(var7);
        if(var5 == 0) {
            this.setDragging(true);
        }
        
        return true;
    }
    
    
    public boolean superMouseReleased(double var1, double var3, int var5) {
        this.setDragging(false);
        return superSuperMouseReleased(var1, var3, var5);
    }
    
    public boolean superSuperMouseReleased(double var1, double var3, int var5) {
        return this.getFocused() != null && this.getFocused().mouseReleased(var1, var3, var5);
    }
    
    public void superDraw(MatrixStack matrices, int var1, int var2, float var3) {
        int var4;
        for(var4 = 0; var4 < this.children().size(); ++var4) {
            if (this.children().get(var4) instanceof ButtonWidget button) {
                button.render(matrices, var1, var2, var3);
            }
        }
        
        //        for(var4 = 0; var4 < this.labelWidgets.size(); ++var4) {
        //            ((LabelWidget) this.labelWidgets.get(var4)).draw(var1, var2, var3);
        //        }
        
    }
}

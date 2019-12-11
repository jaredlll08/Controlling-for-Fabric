package com.blamejared.fabriccontrolling.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Util;

import java.util.Iterator;
import java.util.function.Predicate;

@SuppressWarnings("ALL")
@Environment(EnvType.CLIENT)
public class ControlsSettingsGuiNew extends ControlsOptionsScreen {
    
    private final Screen parent;
    private final GameOptions options;
    private KeyBindingListWidgetNew keyBindingListWidget;
    private ButtonWidget resetButton;
    
    
    private TextFieldWidget search;
    private String lastSearch;
    private DisplayType displayType;
    public ButtonWidget noneBtn;
    public ButtonWidget conflictBtn;
    public SearchType searchType;
    public ButtonWidget sTypeBtn;
    
    public ControlsSettingsGuiNew(Screen var1, GameOptions var2) {
        super(var1, var2);
        this.parent = var1;
        this.options = var2;
    }
    
    protected void init() {
        this.lastSearch = "";
        displayType = DisplayType.ALL;
        searchType = SearchType.NAME;
        this.search = new TextFieldWidget(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, "");
        this.keyBindingListWidget = new KeyBindingListWidgetNew(this, this.minecraft);
        this.children.add(this.keyBindingListWidget);
        this.setFocused(this.keyBindingListWidget);
        this.addButton(new ButtonWidget(this.width / 2 - 155, 18, 150, 20, I18n.translate("options.mouse_settings"), (buttonWidget) -> {
            this.minecraft.openScreen(new MouseOptionsScreen(this, this.gameOptions));
        }));
        this.addButton(Option.AUTO_JUMP.createButton(this.gameOptions, this.width / 2 - 155 + 160, 18, 150));
        
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, "Done", var1 -> ControlsSettingsGuiNew.this.minecraft.openScreen(ControlsSettingsGuiNew.this.parent)));
        conflictBtn = this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, "Show Conflicts", var1 -> {
            if(displayType == DisplayType.CONFLICTS) {
                conflictBtn.setMessage("Show Conflicts");//I18n.translate("options.showConflicts");
                displayType = DisplayType.ALL;
            } else {
                displayType = DisplayType.CONFLICTS;
                conflictBtn.setMessage("Show All");
                noneBtn.setMessage("Show Unbound");//I18n.translate("options.showNone");
            }
            filterKeys();
        }));
        noneBtn = this.addButton(new ButtonWidget(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24, 150 / 2, 20, "Show Unbound", var1 -> {
            if(displayType == DisplayType.UNBOUND) {
                noneBtn.setMessage("Show Unbound");//I18n.translate("options.showNone");
                displayType = DisplayType.ALL;
            } else {
                displayType = DisplayType.UNBOUND;
                noneBtn.setMessage("Show All");
                conflictBtn.setMessage("Show Conflicts");//I18n.translate("options.showConflicts");
            }
            filterKeys();
        }));
        sTypeBtn = this.addButton(new ButtonWidget(this.width / 2 - (155 / 2) + 20, this.height - 29 - 39 - (5), 53, 20, searchType.niceName(), var1 -> {
            searchType = searchType.cycle();
            var1.setMessage(searchType.niceName());
            filterKeys();
        }));
        this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll"), var1 -> {
            KeyBinding[] var2 = this.minecraft.options.keysAll;
            int var3 = var2.length;
            
            for(int var4 = 0; var4 < var3; ++var4) {
                KeyBinding keyBinding_1 = var2[var4];
                keyBinding_1.setKeyCode(keyBinding_1.getDefaultKeyCode());
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
    
    
    public void render(int mx, int my, float pt) {
        
        this.renderBackground();
        this.keyBindingListWidget.render(mx, my, pt);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
        boolean changed = false;
        KeyBinding[] keys = this.options.keysAll;
        
        for(KeyBinding var8 : keys) {
            if(!var8.isDefault()) {
                changed = true;
                break;
            }
        }
        
        this.resetButton.active = changed;
        search.render(mx, my, pt);
        superDraw(mx, my, pt);
        drawCenteredString(font, I18n.translate("options.search"), this.width / 2 - (155 / 2), this.height - 29 - 39, 16777215);
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
        if(displayType == DisplayType.ALL && lastSearch.isEmpty()) {
            keyBindingListWidget.children().addAll(keyBindingListWidget.getAllListeners());
            return;
        }
        Predicate<KeyBindingListWidgetNew.KeyEntry> predicate = displayType.getPred();
        if(!lastSearch.isEmpty()) {
            switch(searchType) {
                case NAME:
                    predicate = predicate.and(keyEntry -> I18n.translate(keyEntry.bindingName).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case KEY:
                    predicate = predicate.and(keyEntry -> I18n.translate(keyEntry.binding.getName()).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case CATEGORY:
                    predicate = predicate.and(keyEntry -> I18n.translate(keyEntry.binding.getCategory()).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
            }
        }
        
        for(KeyBindingListWidgetNew.Entry entry : keyBindingListWidget.getAllListeners()) {
            if(entry instanceof KeyBindingListWidgetNew.KeyEntry) {
                KeyBindingListWidgetNew.KeyEntry ent = (KeyBindingListWidgetNew.KeyEntry) entry;
                if(predicate.test(ent)) {
                    keyBindingListWidget.children().add(ent);
                }
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
                this.options.setKeyCode(this.focusedBinding, InputUtil.UNKNOWN_KEYCODE);
            } else {
                this.options.setKeyCode(this.focusedBinding, InputUtil.getKeyCode(var1, var2));
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
    
    public void superDraw(int var1, int var2, float var3) {
        int var4;
        for(var4 = 0; var4 < this.buttons.size(); ++var4) {
            ((ButtonWidget) this.buttons.get(var4)).render(var1, var2, var3);
        }
        
        //        for(var4 = 0; var4 < this.labelWidgets.size(); ++var4) {
        //            ((LabelWidget) this.labelWidgets.get(var4)).draw(var1, var2, var3);
        //        }
        
    }
}

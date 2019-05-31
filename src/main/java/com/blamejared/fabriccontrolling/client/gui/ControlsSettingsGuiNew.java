package com.blamejared.fabriccontrolling.client.gui;

import net.fabricmc.api.*;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.menu.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.options.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import sun.plugin2.message.Message;

import java.util.Iterator;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class ControlsSettingsGuiNew extends ControlsOptionsScreen {
    
    private static final KeyBinding[] SETTINGS;
    private final Screen parent;
    private final GameOptions settings;
    private KeyBindingListWidgetNew keyBindList;
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
        this.settings = var2;
    }
    
    protected void onInitialized() {
        this.lastSearch = "";
        displayType = DisplayType.ALL;
        searchType = SearchType.NAME;
        this.search = new TextFieldWidget(font, this.width / 2 - 154, this.height - 29 - 23, 148, 18, "");
        this.keyBindList = new KeyBindingListWidgetNew(this, this.minecraft);
        this.children.add(this.keyBindList);
        this.setFocused(this.keyBindList);
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
//                this.addButton(new OptionButtonWidget(var5.getId(), this.width / 2 - 155 + index % 2 * 160, 18 + 24 * (index >> 1), var5, this.settings.getTranslatedName(var5)) {
//                    public void onPressed(double var1, double var3) {
//                        ControlsSettingsGuiNew.this.settings.updateOption(this.getOption(), 1);
//                        this.text = ControlsSettingsGuiNew.this.settings.getTranslatedName(KeyBinding.byId(this.id));
//                    }
//                });
//            }
//            ++index;
//        }
    }
    
    
    public void render(int mx, int my, float pt) {
        this.drawBackground();
        this.keyBindList.draw(mx, my, pt);
        this.drawStringCentered(this.font, this.title, this.width / 2, 8, 16777215);
        boolean changed = false;
        KeyBinding[] keys = this.settings.keysAll;
        
        for(KeyBinding var8 : keys) {
            if(!var8.isDefault()) {
                changed = true;
                break;
            }
        }
        
        this.resetButton.enabled = changed;
        search.render(mx, my, pt);
        superDraw(mx, my, pt);
        drawStringCentered(font, "Search"/*I18n.translate("options.search")*/, this.width / 2 - (155 / 2), this.height - 29 - 39, 16777215);
    }
    
    
    @Override
    public void update() {
        super.update();
        search.tick();
        if(!lastSearch.equals(search.getText())) {
            filterKeys();
        }
    }
    
    public void filterKeys() {
        lastSearch = search.getText();
        keyBindList.getchildren().clear();
        if(displayType == DisplayType.ALL && lastSearch.isEmpty()) {
            keyBindList.getchildren().addAll(keyBindList.getAllchildren());
            return;
        }
        Predicate<KeyBindingListWidgetNew.KeyEntry> predicate = displayType.getPred();
        if(!lastSearch.isEmpty()) {
            switch(searchType) {
                case NAME:
                    predicate = predicate.and(keyEntry -> I18n.translate(keyEntry.keyDesc).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case KEY:
                    predicate = predicate.and(keyEntry -> I18n.translate(keyEntry.keyBinding.getName()).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
                case CATEGORY:
                    predicate = predicate.and(keyEntry -> I18n.translate(keyEntry.keyBinding.method_1423()).toLowerCase().contains(lastSearch.toLowerCase()));
                    break;
            }
        }
        
        for(KeyBindingListWidgetNew.Entry entry : keyBindList.getAllchildren()) {
            if(entry instanceof KeyBindingListWidgetNew.KeyEntry) {
                KeyBindingListWidgetNew.KeyEntry ent = (KeyBindingListWidgetNew.KeyEntry) entry;
                if(predicate.test(ent)) {
                    keyBindList.getchildren().add(ent);
                }
            }
        }
        
    }
    
    @Override
    public boolean charTyped(char var1, int var2) {
        return search.charTyped(var1, var2);
    }
    
    public boolean mouseClicked(double mx, double my, int btn) {
        
        boolean valid;
        if(this.field_2727 != null) {
            search.setHasFocus(false);
            this.settings.method_1641(this.field_2727, InputUtil.Type.KEY_MOUSE.createFromCode(btn));
            this.field_2727 = null;
            KeyBinding.method_1426();
            valid = true;
        } else if(btn == 0 && this.keyBindList.mouseClicked(mx, my, btn)) {
            search.setHasFocus(false);
            this.setActive(true);
            this.setFocused(this.keyBindList);
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
        if(btn == 0 && this.keyBindList.mouseReleased(mx, my, btn)) {
            this.setActive(false);
            return true;
        } else if(search.isFocused()) {
            return search.mouseReleased(mx, my, btn);
        } else {
            return superMouseReleased(mx, my, btn);
        }
    }
    
    
    public boolean keyPressed(int var1, int var2, int var3) {
        if(search.keyPressed(var1, var2, var3)) {
            return true;
        }
        if(this.field_2727 != null) {
            if(var1 == 256) {
                this.settings.method_1641(this.field_2727, InputUtil.field_16237);
            } else {
                this.settings.method_1641(this.field_2727, InputUtil.method_15985(var1, var2));
            }
            
            this.field_2727 = null;
            this.field_2723 = SystemUtil.getMeasuringTimeMili();
            KeyBinding.method_1426();
            return true;
        } else {
            return superKeyPressed(var1, var2, var3);
        }
    }
    
    public boolean superKeyPressed(int var1, int var2, int var3) {
        if(var1 == 256 && this.canClose()) {
            this.close();
            return true;
        } else {
            return super.keyPressed(var1, var2, var3);
        }
    }
    
    
    static {
        SETTINGS = new KeyBinding[] {KeyBinding.INVERT_MOUSE, KeyBinding.SENSITIVITY, KeyBinding.TOUCHSCREEN, KeyBinding.AUTO_JUMP};
    }
    
    
    public boolean superMouseClicked(double var1, double var3, int var5) {
        Iterator var6 = this.getchildren().iterator();
        
        GuiEventListener var7;
        boolean var8;
        do
        {
            if(!var6.hasNext()) {
                return false;
            }
            
            var7 = (GuiEventListener) var6.next();
            var8 = var7.mouseClicked(var1, var3, var5);
        } while(!var8);
        
        this.focusOn(var7);
        if(var5 == 0) {
            this.setActive(true);
        }
        
        return true;
    }
    
    
    public boolean superMouseReleased(double var1, double var3, int var5) {
        this.setActive(false);
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

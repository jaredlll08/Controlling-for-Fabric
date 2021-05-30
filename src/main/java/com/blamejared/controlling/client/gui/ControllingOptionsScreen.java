package com.blamejared.controlling.client.gui;

import com.blamejared.controlling.mixin.ControlsOptionScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("ALL")
@Environment(EnvType.CLIENT)
public class ControllingOptionsScreen extends ControlsOptionsScreen {
    
    private final Screen parent;
    private final GameOptions options;

    private String lastSearch;
    private TextFieldWidget search;

    private ControllingListWidget customKeyList;
    private FreeKeysListWidget freeKeyList;

    private DisplayMode displayMode = DisplayMode.ALL;
    private SearchType searchType = SearchType.NAME;
    private SortOrder sortOrder = SortOrder.NONE;

    public ButtonWidget noneBtn;
    public ButtonWidget conflictBtn;
    public ButtonWidget searchTypeBtn;
    private ButtonWidget freeKeysBtn;
    private boolean confirmingReset;
    private boolean showFree;

    public ControllingOptionsScreen(Screen var1, GameOptions var2) {
        super(var1, var2);
        this.parent = var1;
        this.options = var2;
    }

    @Override
    protected void init() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 18, 150, 20, new TranslatableText("options.mouse_settings"), (buttonWidget) -> {
            this.client.openScreen(new MouseOptionsScreen(this, this.gameOptions));
        }));
        this.addDrawableChild(Option.AUTO_JUMP.createButton(this.gameOptions, this.width / 2 - 155 + 160, 18, 150));
        this.customKeyList = new ControllingListWidget(this, this.client);
        this.freeKeyList = new FreeKeysListWidget(this, this.client);
        this.setKeyBindingListWidget(this.customKeyList);
        this.addDrawableChild(this.getKeyBindingListWidget());
        this.setFocused(this.getKeyBindingListWidget());
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, new TranslatableText("gui.done"), var1 -> ControllingOptionsScreen.this.client.openScreen(ControllingOptionsScreen.this.parent)));

        ((ControlsOptionScreenAccessor) this).setResetButton(this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), button -> {
            if (!confirmingReset) {
                confirmingReset = true;
                button.setMessage(new TranslatableText("options.confirmReset"));
            } else {
                confirmingReset = false;
                button.setMessage(new TranslatableText("controls.resetAll"));
                for(KeyBinding keyBinding : this.client.options.keysAll) {
                    keyBinding.setBoundKey(keyBinding.getDefaultKey());
                }

                KeyBinding.updateKeysByCode();
                this.filterKeys();
            }
        })));
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
        conflictBtn = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29 - 24, 150 / 2, 20, new TranslatableText("options.showConflicts"), var1 -> {
            if(displayMode == DisplayMode.CONFLICTING) {
                conflictBtn.setMessage(new TranslatableText("options.showConflicts"));
                displayMode = DisplayMode.ALL;
            } else {
                displayMode = DisplayMode.CONFLICTING;
                conflictBtn.setMessage(new TranslatableText("options.showAll"));
                noneBtn.setMessage(new TranslatableText("options.showNone"));
            }
            filterKeys();
        }));
        this.search = this.addSelectableChild(new TextFieldWidget(textRenderer, this.width / 2 - 154, this.height - 29 - 23, 148, 18, new LiteralText("")));
        searchTypeBtn = this.addDrawableChild(new ButtonWidget(this.width / 2 - (155 / 2) + 20, this.height - 29 - 39 - (5), 53, 20, new TranslatableText(searchType.niceName()), button -> {
            searchType = searchType.cycle();
            button.setMessage(new TranslatableText(searchType.niceName()));
            filterKeys();
        }));
        ButtonWidget sortOrderBtn = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160 + 76, this.height - 29 - 24 - 24, 150 / 2, 20, getSortMessage(this.sortOrder), (button) -> {
            this.sortOrder = this.sortOrder.cycle();
            button.setMessage(getSortMessage(this.sortOrder));
            this.filterKeys();
        }));

        this.freeKeysBtn = this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29 - 24 - 24, 150 / 2, 20, new TranslatableText("options.toggleFree"), (button) -> {
            this.remove(this.getKeyBindingListWidget());
            if(this.showFree) {
                sortOrderBtn.active = true;
                this.searchTypeBtn.active = true;
                this.noneBtn.active = true;
                this.conflictBtn.active = true;
                this.getResetButton().active = true;
                this.setKeyBindingListWidget(this.customKeyList);
            } else {
                sortOrderBtn.active = false;
                this.freeKeyList.recalculate();
                this.searchTypeBtn.active = false;
                this.noneBtn.active = false;
                this.conflictBtn.active = false;
                this.getResetButton().active = false;
                this.setKeyBindingListWidget(this.freeKeyList);
            }
            this.addDrawableChild(this.getKeyBindingListWidget());
            this.setFocused(this.getKeyBindingListWidget());
            showFree = !showFree;
        }));

        this.lastSearch = "";
    }

    private ButtonWidget getResetButton() {
        return ((ControlsOptionScreenAccessor) this).getResetButton();
    }

    private void setKeyBindingListWidget(CustomListWidget customKeyList) {
        ((ControlsOptionScreenAccessor) this).setKeyBindingListWidget(customKeyList);
    }

    private CustomListWidget getKeyBindingListWidget() {
        return (CustomListWidget) ((ControlsOptionScreenAccessor) this).getKeyBindingListWidget();
    }

    private MutableText getSortMessage(SortOrder sortOrder) {
        return new TranslatableText("options.sort").append(": ").append(sortOrder.getName());
    }

    @Override
    public void render(MatrixStack matrices, int mx, int my, float pt) {
        this.renderBackground(matrices);
        this.getKeyBindingListWidget().render(matrices, mx, my, pt);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xffffff);
        boolean changed = false;
        KeyBinding[] keys = this.options.keysAll;
        
        for(KeyBinding var8 : keys) {
            if(!var8.isDefault()) {
                changed = true;
                break;
            }
        }
        
        this.getResetButton().active = changed;
        search.render(matrices, mx, my, pt);
        superDraw(matrices, mx, my, pt);
        textRenderer.draw(matrices, new TranslatableText("options.search"), this.search.x, this.height - 29 - 39, 0xffffff);
    }
    
    
    @Override
    public void tick() {
        super.tick();
        this.search.tick();
        if(!this.lastSearch.equals(this.search.getText())) {
            this.filterKeys();
        }
    }
    
    public void filterKeys() {
        this.lastSearch = search.getText();
        this.customKeyList.filterKeys(lastSearch, displayMode, searchType, sortOrder);
        this.freeKeyList.filterKeys(lastSearch);
        // Prevent scroll overflow
        this.clampScrollAmount();
    }

    private void clampScrollAmount() {
        // the setter clamps on its own
        this.getKeyBindingListWidget().setScrollAmount(this.getKeyBindingListWidget().getScrollAmount());
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return search.charTyped(chr, modifiers);
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
        } else if(btn == 0 && this.getKeyBindingListWidget().mouseClicked(mx, my, btn)) {
            if(search.isFocused()) {
                search.changeFocus(false);
            }
            this.setDragging(true);
            this.setFocused(this.getKeyBindingListWidget());
            valid = true;
        } else {
            valid = search.mouseClicked(mx, my, btn);
            if(!valid && search.isFocused() && btn == 1) {
                search.setText("");
                valid = true;
            }
        }
        
        if(!valid) {
            this.focusedBinding = null; // ensure we don't set a binding to left click
            valid = super.mouseClicked(mx, my, btn);
        }
        return valid;
    }
    
    
    public boolean mouseReleased(double mx, double my, int btn) {
        if(btn == 0 && this.getKeyBindingListWidget().mouseReleased(mx, my, btn)) {
            this.setDragging(false);
            return true;
        } else if(search.isFocused()) {
            return search.mouseReleased(mx, my, btn);
        } else {
            return super.mouseReleased(mx, my, btn);
        }
    }
    
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focusedBinding == null) {
            if (this.tryUpdateSearch(keyCode, scanCode, modifiers)) {
                return true;
            } else if (this.tryCtrlF()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean tryUpdateSearch(int keyCode, int scanCode, int modifiers) {
        return search.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean tryCtrlF() {
        if (!search.isFocused() && hasControlDown() && InputUtil.isKeyPressed(this.client.getWindow().getHandle(), GLFW.GLFW_KEY_F)) {
            search.setTextFieldFocused(true);
            return true;
        }
        return false;
    }

    public void superDraw(MatrixStack matrices, int var1, int var2, float var3) {
        int var4;
        for(var4 = 0; var4 < this.children().size(); ++var4) {
            if (this.children().get(var4) instanceof ClickableWidget button) {
                button.render(matrices, var1, var2, var3);
            }
        }

        //        for(var4 = 0; var4 < this.labelWidgets.size(); ++var4) {
        //            ((LabelWidget) this.labelWidgets.get(var4)).draw(var1, var2, var3);
        //        }

    }
}

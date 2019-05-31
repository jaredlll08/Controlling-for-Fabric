package com.blamejared.fabriccontrolling.mixin;


import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.crafting.ShapelessRecipe;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class TEst {
    
    @Shadow @Final private Map<Identifier, Recipe> recipeMap;
    
    @Inject(at = @At("TAIL"), method = "onResourceReload")
    private void run(CallbackInfo info) {
        System.out.println("HI!");
        DefaultedList<Ingredient> list = DefaultedList.create();
        list.add(Ingredient.ofStacks(new ItemStack(Blocks.DIRT)));
        this.recipeMap.put(new Identifier("controlling", "test"),new ShapelessRecipe(new Identifier("controlling", "test"), "", new ItemStack(Items.DIAMOND), list));
    }
    
}


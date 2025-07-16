package org.lushplugins.lushrecipes.gui;

import org.bukkit.inventory.ItemStack;
import org.lushplugins.guihandler.annotation.ButtonProvider;
import org.lushplugins.guihandler.annotation.CustomGui;
import org.lushplugins.guihandler.annotation.IconProvider;
import org.lushplugins.guihandler.annotation.Provided;
import org.lushplugins.guihandler.gui.GuiActor;
import org.lushplugins.guihandler.slot.Slot;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;
import org.lushplugins.lushrecipes.utils.CustomItemNaming;

@SuppressWarnings("unused")
@CustomGui(title = "Recipe")
public class RecipeGui {

    @IconProvider({'0','1','2','3','4','5','6','7','8'})
    public ItemStack ingredient(Slot slot, GuiActor actor, @Provided CraftingRecipe recipe) {
        // Characters are treated by their ASCII values, so by negating '0' the ASCII is offset to give actual values
        int recipeSlot = slot.label() - '0';

        DisplayItemStack ingredient = recipe.getIngredient(recipeSlot);
        if (ingredient == null) {
            return null;
        }

        // Temporary solution to provide display names for custom items
        ingredient = CustomItemNaming.apply(ingredient);

        return ingredient.asItemStack(actor.player());
    }

    @IconProvider('o')
    public ItemStack result(Slot slot, GuiActor actor, @Provided CraftingRecipe recipe) {
        return recipe.getResult().asItemStack(actor.player());
    }

    @ButtonProvider('b')
    public void backButton(GuiActor actor) {
        LushRecipes.getInstance().getConfigManager().getRecipesGuiBlueprint().open(actor.player());
    }
}

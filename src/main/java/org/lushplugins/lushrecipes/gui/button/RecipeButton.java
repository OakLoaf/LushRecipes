package org.lushplugins.lushrecipes.gui.button;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.lushplugins.lushlib.gui.button.SimpleItemButton;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.gui.RecipeGui;

public class RecipeButton extends SimpleItemButton {

    public RecipeButton(DisplayItemStack item, NamespacedKey key) {
        super(item, (event) -> {
            Player player = (Player) event.getWhoClicked();
            RecipeGui gui = new RecipeGui(LushRecipes.getInstance().getRecipeHandler().getRecipe(key), player);
            gui.open();
        });
    }
}

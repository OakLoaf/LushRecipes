package org.lushplugins.lushrecipes.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.lushplugins.lushlib.gui.inventory.GuiLayer;
import org.lushplugins.lushlib.gui.inventory.PagedGui;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.gui.button.RecipeButton;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;
import org.lushplugins.lushrecipes.gui.button.UnknownButton;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipesGui extends PagedGui {
    private final DisplayItemStack buttonBase;
    private final List<Integer> recipeSlots;

    public RecipesGui(List<GuiLayer> layers, String title, Player player) {
        super(layers, ChatColorHandler.translate(title), player);

        this.buttonBase = this.getButtons().values().stream()
            .filter(button -> button instanceof UnknownButton unknownButton && unknownButton.getLabel() == 'r')
            .map(button -> ((UnknownButton) button).getItem())
            .findFirst()
            .orElse(null);
        this.recipeSlots = this.getButtons().entrySet().stream()
            .filter(entry -> entry.getValue() instanceof UnknownButton button && button.getLabel() == 'r')
            .map(Map.Entry::getKey)
            .sorted()
            .collect(Collectors.toList());
        this.refresh();
    }

    @Override
    public void refresh() {
        super.refresh();

        List<CraftingRecipe> recipes = LushRecipes.getInstance().getRecipeHandler().getRecipes().stream()
            .sorted(Comparator.comparing(o -> o.getKey().asString()))
            .toList();

        int index = 0;
        for (int slot : this.recipeSlots) {
            if (index >= recipes.size()) {
                break;
            }

            CraftingRecipe recipe = recipes.get(index);
            DisplayItemStack result = recipe.getResult();
            DisplayItemStack displayItemStack = this.buttonBase;
            if (displayItemStack != null) {
                result = DisplayItemStack.builder(displayItemStack)
                    .overwrite(DisplayItemStack.builder(result))
                    .build();
            }

            addButton(slot, new RecipeButton(result, recipe.getKey()));
            index++;
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event, true);
    }
}

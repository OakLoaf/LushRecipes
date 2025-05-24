package org.lushplugins.lushrecipes.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.lushplugins.lushlib.gui.button.Button;
import org.lushplugins.lushlib.gui.inventory.Gui;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;
import org.lushplugins.lushrecipes.gui.button.UnknownButton;

import java.util.Map;

public class RecipeGui extends Gui {
    private final CraftingRecipe recipe;

    public RecipeGui(CraftingRecipe recipe, Player player) {
        super(
            LushRecipes.getInstance().getConfigManager().getRecipeGuiBlueprint().getLayers(),
            recipe.getResult().hasDisplayName() ? ChatColorHandler.translate(recipe.getResult().getDisplayName()) : "Recipe",
            player
        );

        this.recipe = recipe;
        this.refresh();
    }

    @Override
    public void refresh() {
        super.refresh();

        for (Map.Entry<Integer, Button> entry : this.getButtons().entrySet()) {
            int slot = entry.getKey();
            if (!(entry.getValue() instanceof UnknownButton button)) {
                continue;
            }

            char label = button.getLabel();
            if (Character.isDigit(label)) {
                DisplayItemStack ingredient = recipe.getIngredient(label);
                if (ingredient == null) {
                    continue;
                }

                if (ingredient.hasCustomModelData()) {
                    switch (ingredient.getType()) {
                        case Material.APPLE -> {
                            String displayName = null;
                            switch (ingredient.getCustomModelData()) {
                                case 1 -> displayName = "&rCherry";
                                case 2 -> displayName = "&rGrape";
                                case 3 -> displayName = "&rLemon";
                                case 4 -> displayName = "&rOrange";
                                case 5 -> displayName = "&rPeach";
                                case 6 -> displayName = "&rStrawberry";
                                case 7 -> displayName = "&rWither Apple";
                            }

                            if (displayName != null) {
                                ingredient = DisplayItemStack.builder(ingredient)
                                    .setDisplayName(displayName)
                                    .build();
                            }
                        }
                        case Material.PAPER -> {
                            if (ingredient.getCustomModelData() == 101) {
                                ingredient = DisplayItemStack.builder(ingredient)
                                    .setDisplayName("&rSeashell")
                                    .build();
                            }
                        }
                        case null, default -> {}
                    }
                }

                setItem(slot, ingredient.asItemStack(this.getPlayer()));
            } else if (label == 'o') {
                ItemStack item = recipe.getResult().asItemStack(this.getPlayer());
                setItem(slot, item);
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        super.onClick(event, true);
    }
}

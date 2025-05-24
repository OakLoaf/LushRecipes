package org.lushplugins.lushrecipes.api.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushrecipes.api.RecipeAPI;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("DuplicatedCode")
public class CraftListener implements Listener {
    private final RecipeAPI instance;

    public CraftListener(RecipeAPI instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof org.bukkit.inventory.CraftingRecipe craftingRecipe)) {
            return;
        }

        CraftingRecipe storedRecipe = this.instance.getRecipe(craftingRecipe.getKey());
        if (storedRecipe == null) {
            return;
        }

        if (!storedRecipe.canCraft(event.getView().getPlayer())) {
            event.getInventory().setResult(null);
            return;
        }

        if (!storedRecipe.isCustom()) {
            return;
        }

        ItemStack[] ingredients = event.getInventory().getMatrix();
        if (!storedRecipe.matchesRecipe(ingredients)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Recipe recipe = event.getRecipe();
        if (!(recipe instanceof org.bukkit.inventory.CraftingRecipe craftingRecipe)) {
            return;
        }

        CraftingRecipe storedRecipe = this.instance.getRecipe(craftingRecipe.getKey());
        if (storedRecipe == null) {
            return;
        }

        if (!storedRecipe.canCraft(event.getWhoClicked())) {
            event.setCancelled(true);
            return;
        }

        if (!storedRecipe.isCustom()) {
            return;
        }

        ItemStack[] ingredients;
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            ingredients = shapedRecipe.getChoiceMap().values().stream()
                .map(choice -> choice != null ? choice.getItemStack() : null)
                .toArray(ItemStack[]::new);
        } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            ingredients = shapelessRecipe.getChoiceList().stream()
                .map(choice -> choice != null ? choice.getItemStack() : null)
                .toArray(ItemStack[]::new);
        } else {
            return;
        }

        List<DisplayItemStack> unmatchedIngredients = Arrays.stream(storedRecipe.getIngredients())
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));

        for (ItemStack ingredient : ingredients) {
            if (ingredient == null) {
                continue;
            }

            for (DisplayItemStack unmatchedIngredient : Collections.unmodifiableCollection(unmatchedIngredients)) {
                if (unmatchedIngredient == null) {
                    continue;
                }

                if (unmatchedIngredient.isSimilar(ingredient)) {
                    ingredient.setAmount(ingredient.getAmount() - unmatchedIngredient.getAmount().getMin());
                    unmatchedIngredients.remove(unmatchedIngredient);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDiscoverRecipe(PlayerRecipeDiscoverEvent event) {
        if (!this.instance.shouldShowInRecipeBook() && this.instance.getRecipe(event.getRecipe()) != null) {
            event.setCancelled(true);
        }
    }
}

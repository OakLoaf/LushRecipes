package org.lushplugins.lushrecipes.api.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lushplugins.lushrecipes.api.RecipeAPI;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;

import java.util.HashSet;
import java.util.Set;

public class PlayerListener implements Listener {
    private final RecipeAPI instance;

    public PlayerListener(RecipeAPI instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.instance.shouldShowInRecipeBook()) {
            Set<NamespacedKey> unlockedRecipes = new HashSet<>();
            Set<NamespacedKey> lockedRecipes = new HashSet<>();
            for (CraftingRecipe recipe : this.instance.getRecipes()) {
                if (recipe.canCraft(player)) {
                    unlockedRecipes.add(recipe.getKey());
                } else {
                    lockedRecipes.add(recipe.getKey());
                }
            }

            player.discoverRecipes(unlockedRecipes);
            player.undiscoverRecipes(lockedRecipes);
        } else {
            player.undiscoverRecipes(this.instance.getRecipeKeys());
        }
    }
}

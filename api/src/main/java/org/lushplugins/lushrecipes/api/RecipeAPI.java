package org.lushplugins.lushrecipes.api;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.lushplugins.lushrecipes.api.listener.CraftListener;
import org.lushplugins.lushrecipes.api.listener.PlayerListener;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;

import java.util.*;

@SuppressWarnings("unused")
public class RecipeAPI {
    private final List<Listener> listeners = new ArrayList<>();
    private final Map<NamespacedKey, CraftingRecipe> recipes;
    private final boolean showInRecipeBook;

    private RecipeAPI(JavaPlugin plugin, Map<NamespacedKey, CraftingRecipe> recipes, boolean showInRecipeBook, boolean discoverRecipesOnJoin) {
        this.recipes = recipes;
        this.showInRecipeBook = showInRecipeBook;

        registerListener(new CraftListener(this), plugin);

        if (discoverRecipesOnJoin) {
            registerListener(new PlayerListener(this), plugin);
        }
    }

    private void registerListener(Listener listener, JavaPlugin plugin) {
        this.listeners.add(listener);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void registerRecipe(CraftingRecipe recipe) {
        NamespacedKey key = recipe.getKey();
        this.recipes.put(key, recipe);

        Recipe bukkitRecipe = Bukkit.getRecipe(key);
        if (bukkitRecipe != null) {
            Bukkit.removeRecipe(key);
        }

        Bukkit.addRecipe(recipe.createBukkitRecipe(), true);

        if (this.showInRecipeBook) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (recipe.canCraft(player)) {
                    player.discoverRecipe(key);
                }
            }
        }
    }

    public void registerRecipes(Map<NamespacedKey, CraftingRecipe> recipes) {
        this.recipes.putAll(recipes);

        for (CraftingRecipe recipe : recipes.values()) {
            NamespacedKey key = recipe.getKey();
            Recipe bukkitRecipe = Bukkit.getRecipe(key);
            if (bukkitRecipe != null) {
                Bukkit.removeRecipe(key);
            }

            Bukkit.addRecipe(recipe.createBukkitRecipe(), true);
        }

        if (this.showInRecipeBook) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                List<NamespacedKey> keys = recipes.values().stream()
                    .filter(recipe -> recipe.canCraft(player))
                    .map(CraftingRecipe::getKey)
                    .toList();

                if (!keys.isEmpty()) {
                    player.discoverRecipes(keys);
                }
            }
        }
    }

    public void unregisterRecipe(NamespacedKey key) {
        this.recipes.remove(key);
    }

    public void clearRecipes() {
        this.recipes.clear();
    }

    public CraftingRecipe getRecipe(NamespacedKey key) {
        return this.recipes.get(key);
    }

    public Set<NamespacedKey> getRecipeKeys() {
        return this.recipes.keySet();
    }

    public Collection<CraftingRecipe> getRecipes() {
        return this.recipes.values();
    }

    @Unmodifiable
    public Map<NamespacedKey, CraftingRecipe> getRecipeMap() {
        return Collections.unmodifiableMap(this.recipes);
    }

    public boolean shouldShowInRecipeBook() {
        return this.showInRecipeBook;
    }

    public void evaluateRecipeDiscovery(Player player) {
        if (this.showInRecipeBook) {
            Set<NamespacedKey> unlockedRecipes = new HashSet<>();
            Set<NamespacedKey> lockedRecipes = new HashSet<>();
            for (CraftingRecipe recipe : this.getRecipes()) {
                if (recipe.canCraft(player)) {
                    unlockedRecipes.add(recipe.getKey());
                } else {
                    lockedRecipes.add(recipe.getKey());
                }
            }

            player.discoverRecipes(unlockedRecipes);
            player.undiscoverRecipes(lockedRecipes);
        } else {
            player.undiscoverRecipes(this.getRecipeKeys());
        }
    }

    public void unregisterListeners() {
        this.listeners.forEach(HandlerList::unregisterAll);
        this.listeners.clear();
    }

    public static Builder builder(JavaPlugin plugin) {
        return new Builder(plugin);
    }

    public static class Builder {
        private final JavaPlugin plugin;
        private final Map<NamespacedKey, CraftingRecipe> recipes = new HashMap<>();
        private boolean showInRecipeBook = false;
        private boolean discoverRecipesOnJoin = true;

        private Builder(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public Builder addRecipe(NamespacedKey key, CraftingRecipe recipe) {
            this.recipes.put(key, recipe);
            return this;
        }

        public Builder addRecipes(Map<NamespacedKey, CraftingRecipe> recipes) {
            this.recipes.putAll(recipes);
            return this;
        }

        public Builder removeRecipe(NamespacedKey key) {
            this.recipes.remove(key);
            return this;
        }

        public Builder showInRecipeBook(boolean showInRecipeBook) {
            this.showInRecipeBook = showInRecipeBook;
            return this;
        }

        public Builder discoverRecipesOnJoin(boolean discoverRecipesOnJoin) {
            this.discoverRecipesOnJoin = discoverRecipesOnJoin;
            return this;
        }

        public RecipeAPI build() {
            return new RecipeAPI(this.plugin, this.recipes, this.showInRecipeBook, this.discoverRecipesOnJoin);
        }
    }
}

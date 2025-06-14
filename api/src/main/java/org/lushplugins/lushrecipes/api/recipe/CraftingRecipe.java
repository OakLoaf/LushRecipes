package org.lushplugins.lushrecipes.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.utils.DisplayItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CraftingRecipe {
    private final NamespacedKey key;
    private final DisplayItemStack[] ingredients;
    private final DisplayItemStack result;
    private final boolean shapeless;
    private final boolean inRecipeBook;
    private final Predicate<HumanEntity> craftPredicate;

    protected CraftingRecipe(NamespacedKey key, DisplayItemStack[] ingredients, DisplayItemStack result, boolean shapeless, boolean inRecipeBook, Predicate<HumanEntity> craftPredicate) {
        this.key = key;
        this.ingredients = ingredients;
        this.result = result;
        this.shapeless = shapeless;
        this.inRecipeBook = inRecipeBook;
        this.craftPredicate = craftPredicate;
    }

    protected CraftingRecipe(NamespacedKey key, DisplayItemStack[] ingredients, DisplayItemStack result, boolean shapeless, boolean inRecipeBook) {
        this(key, ingredients, result, shapeless, inRecipeBook, (crafter) -> true);
    }

    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    public DisplayItemStack[] getIngredients() {
        return this.ingredients;
    }

    /**
     * @param slot crafting slot 0-8
     */
    public @Nullable DisplayItemStack getIngredient(int slot) {
        return this.ingredients[slot];
    }

    public DisplayItemStack getResult() {
        return this.result;
    }

    public boolean isShapeless() {
        return this.shapeless;
    }

    public boolean showInRecipeBook() {
        return this.inRecipeBook;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isCustom() {
        return !this.inRecipeBook || Arrays.stream(this.ingredients).anyMatch(ingredient -> ingredient != null && ingredient.hasMeta());
    }

    public boolean matchesRecipe(ItemStack[] ingredients) {
        if (this.shapeless) {
            return matchesShapeless(ingredients);
        } else {
            return matchesShaped(ingredients);
        }
    }

    public boolean matchesShaped(ItemStack[] ingredients) {
        for (int slot = 0; slot < ingredients.length; slot++) {
            ItemStack ingredient = ingredients[slot];
            DisplayItemStack recipeIngredient = this.ingredients[slot];

            if (ingredient == null) {
                if (recipeIngredient != null) {
                    return false;
                } else {
                    continue;
                }
            }

            if (recipeIngredient == null) {
                return false;
            }

            if (!recipeIngredient.isSimilar(ingredient)) {
                return false;
            }
        }

        return true;
    }

    public boolean matchesShapeless(ItemStack[] ingredients) {
        List<DisplayItemStack> unmatchedIngredients = Arrays.stream(this.ingredients)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));

        for (ItemStack ingredient : ingredients) {
            if (ingredient == null) {
                continue;
            }

            boolean found = false;
            for (DisplayItemStack unmatchedIngredient : Collections.unmodifiableCollection(unmatchedIngredients)) {
                if (unmatchedIngredient == null) {
                    continue;
                }

                if (unmatchedIngredient.isSimilar(ingredient)) {
                    found = true;
                    unmatchedIngredients.remove(unmatchedIngredient);
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }

        return unmatchedIngredients.isEmpty();
    }

    public Predicate<HumanEntity> getCraftPredicate() {
        return craftPredicate;
    }

    public boolean canCraft(HumanEntity crafter) {
        return this.craftPredicate.test(crafter);
    }

    public org.bukkit.inventory.CraftingRecipe createBukkitRecipe() {
        if (this.shapeless) {
            ShapelessRecipe recipe = new ShapelessRecipe(this.key, this.result.asItemStack());

            for (DisplayItemStack ingredient : this.ingredients) {
                if (ingredient == null) {
                    continue;
                }

                Material material = ingredient.getType();
                if (material == null || material.isAir()) {
                    continue;
                }

                recipe.addIngredient(material);
            }

            return recipe;
        } else {
            ShapedRecipe recipe = new ShapedRecipe(this.key, this.result.asItemStack());

            char currChar = 'a';
            String[] shape = new String[]{"", "", ""};
            Map<Character, Material> choiceMap = new HashMap<>();
            for (int slot = 0; slot < 9; slot++) {
                int row = slot / 3;
                DisplayItemStack ingredient = this.ingredients[slot];
                if (ingredient == null) {
                    shape[row] += ' ';
                    continue;
                }

                Material material = ingredient.getType();
                if (material == null || material.isAir()) {
                    shape[row] += ' ';
                    continue;
                }

                choiceMap.put(currChar, ingredient.getType());
                shape[row] += currChar;
                currChar++;
            }

            recipe.shape(shape);
            choiceMap.forEach(recipe::setIngredient);

            return recipe;
        }
    }

    public static Builder builder(NamespacedKey key) {
        return new Builder(key);
    }

    public static class Builder {
        private final NamespacedKey key;
        private DisplayItemStack[] ingredients = new DisplayItemStack[9];
        private DisplayItemStack result;
        private boolean shapeless = false;
        private boolean inRecipeBook = true;
        private Predicate<HumanEntity> craftPredicate = (crafter) -> true;

        private Builder(@NotNull NamespacedKey key) {
            this.key = key;
        }

        public Builder ingredients(DisplayItemStack[] ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        /**
         * @param ingredient the ingredient to add
         */
        public Builder addIngredient(@NotNull DisplayItemStack ingredient) {
            for (int i = 0; i < this.ingredients.length; i++) {
                if (this.ingredients[i] == null) {
                    this.ingredients[i] = ingredient;
                    return this;
                }
            }

            throw new IllegalArgumentException("No available slots");
        }

        /**
         * @param ingredient the ingredient to add
         * @param slot crafting slot 0-8
         */
        public Builder addIngredient(@NotNull DisplayItemStack ingredient, int slot) {
            if (slot < 0 || slot > 8) {
                throw new IllegalArgumentException("Slot out of bounds: " + slot);
            }

            this.ingredients[slot] = ingredient;
            return this;
        }

        public Builder result(@NotNull DisplayItemStack result) {
            this.result = result;
            return this;
        }

        /**
         * @param shapeless whether the recipe should be shapeless
         */
        public Builder shapeless(boolean shapeless) {
            this.shapeless = shapeless;
            return this;
        }

        /**
         * @param inRecipeBook whether the recipe should show in the recipe book
         */
        public Builder showInRecipeBook(boolean inRecipeBook) {
            this.inRecipeBook = inRecipeBook;
            return this;
        }

        /**
         * @param craftPredicate the predicate that must be matched for a recipe to be crafted
         */
        public Builder craftPredicate(Predicate<HumanEntity> craftPredicate) {
            this.craftPredicate = craftPredicate;
            return this;
        }

        /**
         * @return a built recipe
         */
        public CraftingRecipe build() {
            if (this.result == null) {
                throw new IllegalArgumentException("Crafting recipe requires a result");
            }

            return new CraftingRecipe(this.key, this.ingredients, this.result, this.shapeless, this.inRecipeBook, this.craftPredicate);
        }
    }
}

package org.lushplugins.lushrecipes.hook.configurableprocesses.action;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.configurableprocesses.process.action.ConfigurableAction;
import org.lushplugins.configurableprocesses.process.eventdata.EventData;
import org.lushplugins.lushlib.config.YamlConverter;
import org.lushplugins.lushlib.config.YamlUtils;
import org.lushplugins.lushlib.item.DisplayItemStack;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;

import java.util.List;

public class CustomRecipeAction extends ConfigurableAction implements Listener {
    private final CraftingRecipe recipe;

    public CustomRecipeAction(ConfigurationSection config) {
        super(config);

        NamespacedKey key = NamespacedKey.fromString(config.getString("key"));
        boolean shapeless = config.getBoolean("shapeless");
        boolean showInRecipeBook = config.getBoolean("show-in-recipe-book", true);

        DisplayItemStack[] ingredients = new DisplayItemStack[9];
        List<ConfigurationSection> ingredientSections = YamlUtils.getConfigurationSections(config, "ingredients");
        for (ConfigurationSection ingredientSection : ingredientSections) {
            DisplayItemStack ingredient = YamlConverter.getDisplayItem(ingredientSection);
            if (shapeless) {
                for(int i = 0; i < ingredients.length; ++i) {
                    if (ingredients[i] == null) {
                        ingredients[i] = ingredient;
                        break;
                    }
                }
            } else {
                int slot = Integer.parseInt(ingredientSection.getName());
                ingredients[slot] = ingredient;
            }
        }

        ConfigurationSection resultSection = config.getConfigurationSection("result");
        DisplayItemStack result = resultSection != null ? YamlConverter.getDisplayItem(resultSection) : null;

        this.recipe = CraftingRecipe.builder(key)
            .ingredients(ingredients)
            .result(result)
            .shapeless(shapeless)
            .showInRecipeBook(showInRecipeBook)
            .build();

        LushRecipes.getInstance().getRecipeHandler().registerRecipe(this.recipe);
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public void run(EventData eventData, @Nullable Event event) {}

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }
}

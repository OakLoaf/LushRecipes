package org.lushplugins.lushrecipes.config;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushlib.utils.YamlUtils;
import org.lushplugins.lushlib.utils.converter.YamlConverter;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.api.event.PostPopulateRecipesEvent;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;
import org.lushplugins.lushrecipes.gui.RecipeGui;
import org.lushplugins.lushrecipes.gui.RecipesGui;
import org.lushplugins.lushrecipes.utils.YamlUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {
    private boolean showInRecipeBook;
    private Gui.Builder recipesGuiBlueprint;
    private Gui.Builder recipeGuiBlueprint;
    private DisplayItemStack recipeTemplate;
    private List<CraftingRecipe> visualRecipes;

    public ConfigManager() {
        LushRecipes plugin = LushRecipes.getInstance();
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
            plugin.saveDefaultResource("recipes/example-recipes.yml");
        }

        plugin.saveDefaultConfig();
    }

    public void reloadConfig() {
        LushRecipes.getInstance().reloadConfig();
        ConfigurationSection config = LushRecipes.getInstance().getConfig();
        this.showInRecipeBook = config.getBoolean("show-in-recipe-book", true);

        this.visualRecipes = new ArrayList<>();
        LushRecipes.getInstance().getRecipeHandler().clearRecipes();
        loadRecipesFromDirectory(new File(LushRecipes.getInstance().getDataFolder(), "recipes"));

        applyRecipes();
        Bukkit.getServer().getPluginManager().callEvent(new PostPopulateRecipesEvent(LushRecipes.getInstance().getRecipeHandler()));

        ConfigurationSection recipesGuiSection = config.getConfigurationSection("recipes-gui");
        this.recipesGuiBlueprint = YamlUtil.prepareGuiBuilder(recipesGuiSection, new RecipesGui());

        ConfigurationSection recipeGuiSection = config.getConfigurationSection("recipe-gui");
        this.recipeGuiBlueprint = YamlUtil.prepareGuiBuilder(recipeGuiSection, new RecipeGui());
    }

    public void applyRecipes() {
        Collection<CraftingRecipe> recipes = LushRecipes.getInstance().getRecipeHandler().getRecipes();

        for (CraftingRecipe recipe : recipes) {
            NamespacedKey key = recipe.getKey();
            Recipe bukkitRecipe = Bukkit.getRecipe(key);
            if (bukkitRecipe != null) {
                Bukkit.removeRecipe(key);
            }

            bukkitRecipe = recipe.createBukkitRecipe();
            Bukkit.addRecipe(bukkitRecipe, true);
        }

        if (this.showInRecipeBook) {
            Collection<NamespacedKey> recipeKeys = recipes.stream().map(CraftingRecipe::getKey).toList();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.discoverRecipes(recipeKeys);
            }
        }
    }

    public boolean showInRecipeBook() {
        return showInRecipeBook;
    }

    public Gui.Builder getRecipesGuiBlueprint() {
        return this.recipesGuiBlueprint;
    }

    public Gui.Builder getRecipeGuiBlueprint() {
        return this.recipeGuiBlueprint;
    }

    public DisplayItemStack getRecipeTemplate() {
        return recipeTemplate;
    }

    public void setRecipeTemplate(DisplayItemStack recipeTemplate) {
        this.recipeTemplate = recipeTemplate;
    }

    public List<CraftingRecipe> getVisualRecipes() {
        return visualRecipes;
    }

    public void addVisualOnlyRecipe(CraftingRecipe recipe) {
        this.visualRecipes.add(recipe);
    }

    public void removeVisualOnlyRecipe(CraftingRecipe recipe) {
        this.visualRecipes.remove(recipe);
    }

    /**
     * Read all drop files in a directory
     * @param directory directory to read from
     */
    private void loadRecipesFromDirectory(File directory) {
        try (
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(directory.toPath(), "*.yml")
        ) {
            for (Path filePath : fileStream) {
                File file = filePath.toFile();
                loadRecipesFromFile(file);
            }
        } catch (IOException e) {
            LushRecipes.getInstance().getLogger().log(Level.WARNING, "Caught error whilst loading drops: ", e);
        }
    }

    private void loadRecipesFromFile(File file) {
        ConfigurationSection config = YamlConfiguration.loadConfiguration(file);
        if (!config.getBoolean("enabled", true)) {
            return;
        }

        config.getValues(false).forEach((key, value) -> {
            if (value instanceof ConfigurationSection dropConfig) {
                loadRecipesFromConfig(dropConfig);
            }
        });
    }

    private void loadRecipesFromConfig(ConfigurationSection config) {
        NamespacedKey key = NamespacedKey.fromString(config.getName());
        CraftingRecipe.Builder recipeBuilder = CraftingRecipe.builder(key);

        boolean visualOnly = config.getBoolean("visual-only", false);
        boolean shapeless = config.getBoolean("shapeless", false);
        recipeBuilder.shapeless(shapeless);
        recipeBuilder.showInRecipeBook(this.showInRecipeBook);

        List<ConfigurationSection> ingredientSections = YamlUtils.getConfigurationSections(config, "ingredients");
        for (ConfigurationSection ingredientSection : ingredientSections) {
            DisplayItemStack ingredient = YamlConverter.getDisplayItem(ingredientSection);
            if (shapeless) {
                recipeBuilder.addIngredient(ingredient);
            } else {
                int slot = Integer.parseInt(ingredientSection.getName());
                recipeBuilder.addIngredient(ingredient, slot);
            }
        }

        ConfigurationSection resultSection = config.getConfigurationSection("result");
        if (resultSection != null) {
            recipeBuilder.result(YamlConverter.getDisplayItem(resultSection));
        }

        try {
            CraftingRecipe recipe = recipeBuilder.build();
            if (visualOnly) {
                this.addVisualOnlyRecipe(recipe);
            } else {
                LushRecipes.getInstance().getRecipeHandler().registerRecipe(recipe);
            }
        } catch (IllegalArgumentException e) {
            LushRecipes.getInstance().getLogger().log(Level.WARNING, "Failed to load recipe: " + config.getName(), e);
        }
    }
}

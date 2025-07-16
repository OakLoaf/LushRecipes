package org.lushplugins.lushrecipes;

import org.lushplugins.guihandler.GuiHandler;
import org.lushplugins.guihandler.slot.Button;
import org.lushplugins.guihandler.slot.IconProvider;
import org.lushplugins.guihandler.slot.SlotProvider;
import org.lushplugins.lushlib.LushLib;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.lushrecipes.api.RecipeAPI;
import org.lushplugins.lushrecipes.command.RecipesCommand;
import org.lushplugins.lushrecipes.config.ConfigManager;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public final class LushRecipes extends SpigotPlugin {
    private static LushRecipes plugin;

    private RecipeAPI recipeHandler;
    private GuiHandler guiHandler;
    private ConfigManager configManager;

    @Override
    public void onLoad() {
        plugin = this;
        LushLib.getInstance().enable(this);
    }

    @Override
    public void onEnable() {
        this.recipeHandler = RecipeAPI.builder(this).build();
        this.guiHandler = GuiHandler.builder(this)
            .registerLabelProvider(' ', new SlotProvider())
            .registerLabelProvider('>', new SlotProvider().button((context) -> context.gui().nextPage()))
            .registerLabelProvider('<', new SlotProvider().button((context) -> {
                if (context.gui().page() > 1) {
                    context.gui().previousPage();
                }
            }))
            .registerLabelProvider('b', new SlotProvider().button((context) -> {
                LushRecipes.getInstance().getConfigManager().getRecipesGuiBlueprint()
                    .open(context.gui().actor().player());
            }))
            .build();

        this.configManager = new ConfigManager();
        this.configManager.reloadConfig();

        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).build();
        lamp.register(new RecipesCommand());
    }

    @Override
    public void onDisable() {
        // Disable implementation
    }

    public RecipeAPI getRecipeHandler() {
        return recipeHandler;
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public static LushRecipes getInstance() {
        return plugin;
    }
}

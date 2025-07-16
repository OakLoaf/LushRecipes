package org.lushplugins.lushrecipes.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushrecipes.api.RecipeAPI;

public class PostPopulateRecipesEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final RecipeAPI recipeHandler;

    public PostPopulateRecipesEvent(RecipeAPI recipeHandler) {
        this.recipeHandler = recipeHandler;
    }

    public RecipeAPI getRecipeHandler() {
        return recipeHandler;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}

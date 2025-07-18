package org.lushplugins.lushrecipes.gui;

import com.google.common.collect.Streams;
import org.bukkit.entity.Player;
import org.lushplugins.guihandler.annotation.ButtonProvider;
import org.lushplugins.guihandler.annotation.CustomGui;
import org.lushplugins.guihandler.annotation.GuiEvent;
import org.lushplugins.guihandler.annotation.Slots;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.guihandler.gui.GuiAction;
import org.lushplugins.guihandler.gui.GuiActor;
import org.lushplugins.guihandler.slot.IconProvider;
import org.lushplugins.guihandler.slot.Slot;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.api.recipe.CraftingRecipe;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@CustomGui
public class RecipesGui {

    @GuiEvent(GuiAction.REFRESH)
    public void recipes(Gui gui, @Slots('r') List<Slot> slots) {
        GuiActor actor = gui.actor();
        ArrayDeque<CraftingRecipe> recipes = getPageContent(actor, gui.page(), slots.size());

        for (Slot slot : slots) {
            if (recipes.isEmpty()) {
                slot.iconProvider(IconProvider.EMPTY);
                continue;
            }

            CraftingRecipe recipe = recipes.pop();
            DisplayItemStack result = recipe.getResult();

            DisplayItemStack template = LushRecipes.getInstance().getConfigManager().getRecipeTemplate();
            if (template != null) {
                result = DisplayItemStack.builder(template)
                    .overwrite(DisplayItemStack.builder(result))
                    .build();
            }

            slot.icon(result.asItemStack(actor.player()));
            slot.button((context) -> {
                Gui.Builder recipeGui = LushRecipes.getInstance().getConfigManager().getRecipeGuiBlueprint();

                Player player = context.gui().actor().player();
                if (recipe.getResult().hasDisplayName()) {
                    recipeGui.openWith(player, recipe.getResult().getDisplayName(), recipe);
                } else {
                    recipeGui.open(player, recipe);
                }
            });
        }
    }

    private Stream<CraftingRecipe> getContentStream(GuiActor actor) {
        return Streams.concat(
                LushRecipes.getInstance().getRecipeHandler().getRecipes().stream(),
                LushRecipes.getInstance().getConfigManager().getVisualRecipes().stream())
            .filter(recipe -> recipe.canCraft(actor.player()));
    }

    private Stream<CraftingRecipe> getPageContentStream(GuiActor actor, int page, int pageSize) {
        return getContentStream(actor)
            .sorted(Comparator.comparing(o -> o.getKey().asString()))
            .skip((long) (page - 1) * pageSize)
            .limit(pageSize);
    }

    private ArrayDeque<CraftingRecipe> getPageContent(GuiActor actor, int page, int pageSize) {
        return getPageContentStream(actor, page, pageSize).collect(Collectors.toCollection(ArrayDeque::new));
    }

    @ButtonProvider('>')
    public void nextPageButton(Gui gui, @Slots('r') List<Slot> slots) {
        int pageSize = slots.size();
        Stream<CraftingRecipe> content = getPageContentStream(gui.actor(), gui.page(), pageSize);
        if (content.count() == pageSize) {
            gui.nextPage();
        }
    }

    @ButtonProvider('<')
    public void prevPageButton(Gui gui) {
        if (gui.page() > 1) {
            gui.previousPage();
        }
    }
}

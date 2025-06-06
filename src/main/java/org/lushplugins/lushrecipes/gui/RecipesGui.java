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

@SuppressWarnings("unused")
@CustomGui
public class RecipesGui {

    @GuiEvent(GuiAction.REFRESH)
    public void recipes(GuiActor actor, @Slots('r') List<Slot> slots) {
        ArrayDeque<CraftingRecipe> recipes = Streams.concat(
                LushRecipes.getInstance().getRecipeHandler().getRecipes().stream(),
                LushRecipes.getInstance().getConfigManager().getVisualRecipes().stream())
            .filter(recipe -> recipe.canCraft(actor.player()))
            .sorted(Comparator.comparing(o -> o.getKey().asString()))
            .collect(Collectors.toCollection(ArrayDeque::new));

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
                Gui.Builder gui = LushRecipes.getInstance().getConfigManager().getRecipeGuiBlueprint();

                Player player = context.gui().actor().player();
                if (recipe.getResult().hasDisplayName()) {
                    gui.openWith(player, recipe.getResult().getDisplayName(), recipe);
                } else {
                    gui.open(player, recipe);
                }
            });
        }
    }

    @ButtonProvider('>')
    public void nextPage(Gui gui) {
        gui.nextPage();
    }

    @ButtonProvider('<')
    public void previousPage(Gui gui) {
        gui.previousPage();
    }
}

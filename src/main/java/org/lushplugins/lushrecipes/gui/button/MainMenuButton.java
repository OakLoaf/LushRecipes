package org.lushplugins.lushrecipes.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.lushplugins.lushlib.gui.button.GuiButton;
import org.lushplugins.lushlib.gui.inventory.Gui;
import org.lushplugins.lushlib.gui.inventory.GuiBlueprint;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.gui.RecipesGui;

public class MainMenuButton extends GuiButton {

    public MainMenuButton(DisplayItemStack item) {
        super(item);
    }

    @Override
    public void onClick(Gui gui, InventoryClickEvent event) {
        GuiBlueprint blueprint = LushRecipes.getInstance().getConfigManager().getRecipesGuiBlueprint();
        blueprint.construct((Player) event.getWhoClicked(), RecipesGui::new).open();
    }
}

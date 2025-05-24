package org.lushplugins.lushrecipes.command;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.gui.inventory.GuiBlueprint;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.gui.RecipesGui;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@SuppressWarnings("unused")
@Command("recipes")
public class RecipesCommand {

    @Command("recipes")
    public void recipes(BukkitCommandActor actor) {
        actor.requirePlayer();

        GuiBlueprint blueprint = LushRecipes.getInstance().getConfigManager().getRecipesGuiBlueprint();
        blueprint.construct(actor.asPlayer(), RecipesGui::new).open();
    }

    @Subcommand("reload")
    @CommandPermission("lushrecipes.reload")
    public void reload(BukkitCommandActor actor) {
        LushRecipes.getInstance().getConfigManager().reloadConfig();
        ChatColorHandler.sendMessage(actor.sender(), "&#b7faa2LushRecipes has been reloaded &#66b04f🔃");
    }
}

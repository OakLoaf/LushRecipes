package org.lushplugins.lushrecipes.command;

import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushrecipes.LushRecipes;
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

        LushRecipes.getInstance().getConfigManager().getRecipesGuiBlueprint().open(actor.asPlayer());
    }

    @Subcommand("reload")
    @CommandPermission("lushrecipes.reload")
    public void reload(BukkitCommandActor actor) {
        LushRecipes.getInstance().getConfigManager().reloadConfig();
        ChatColorHandler.sendMessage(actor.sender(), "&#b7faa2LushRecipes has been reloaded &#66b04f🔃");
    }
}

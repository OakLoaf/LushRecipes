package org.lushplugins.lushrecipes.hook.configurableprocesses;

import org.lushplugins.configurableprocesses.ConfigurableProcesses;
import org.lushplugins.configurableprocesses.plugin.ConfigurableProcessesPlugin;
import org.lushplugins.lushrecipes.LushRecipes;
import org.lushplugins.lushrecipes.hook.configurableprocesses.action.CustomRecipeAction;

public class ConfigurableProcessesHook {

    public static void register(LushRecipes plugin) {
        ConfigurableProcesses configurableProcesses = ConfigurableProcessesPlugin.api();
        configurableProcesses.actions().register(plugin, "custom_recipe", CustomRecipeAction::new);
    }
}

package org.lushplugins.lushrecipes;

import org.bukkit.plugin.java.JavaPlugin;

public final class LushRecipes extends JavaPlugin {
    private static LushRecipes plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Enable implementation
    }

    @Override
    public void onDisable() {
        // Disable implementation
    }

    public static LushRecipes getInstance() {
        return plugin;
    }
}

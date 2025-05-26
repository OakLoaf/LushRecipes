package org.lushplugins.lushrecipes.api.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.lushplugins.lushrecipes.api.RecipeAPI;

public class PlayerListener implements Listener {
    private final RecipeAPI instance;

    public PlayerListener(RecipeAPI instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.instance.evaluateRecipeDiscovery(event.getPlayer());
    }
}

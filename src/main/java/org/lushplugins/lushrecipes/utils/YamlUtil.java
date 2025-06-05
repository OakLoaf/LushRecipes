package org.lushplugins.lushrecipes.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.guihandler.gui.GuiLayer;
import org.lushplugins.guihandler.slot.Button;
import org.lushplugins.guihandler.slot.SlotProvider;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushlib.utils.YamlUtils;
import org.lushplugins.lushlib.utils.converter.YamlConverter;
import org.lushplugins.lushrecipes.LushRecipes;

public class YamlUtil {

    public static Gui.Builder prepareGuiBuilder(ConfigurationSection config, Object instance) {
        if (config == null) {
            return null;
        }

        String title = config.getString("title");
        GuiLayer layer = new GuiLayer(config.getStringList("format"));
        for (ConfigurationSection buttonSection : YamlUtils.getConfigurationSections(config, "buttons")) {
            DisplayItemStack item = YamlConverter.getDisplayItem(buttonSection);

            char label = buttonSection.getName().charAt(0);
            Button button = switch (buttonSection.getString("type")) {
                case "back" -> (context) -> LushRecipes.getInstance().getConfigManager().getRecipesGuiBlueprint()
                    .open(context.gui().actor().player());
                case "previous_page" -> (context) -> context.gui().previousPage();
                case "next_page" -> (context) -> context.gui().nextPage();
                case null, default -> Button.EMPTY;
            };

            layer.setSlotProvider(label, new SlotProvider(
                (context) -> item.hasType() ? item.asItemStack(context.gui().actor().player()) : null,
                button
            ));

            if (label == 'r') {
                LushRecipes.getInstance().getConfigManager().setRecipeTemplate(item);
            }
        }

        Gui.Builder builder;
        if (instance != null) {
            builder = LushRecipes.getInstance().getGuiHandler().prepare(instance);
        } else {
            builder = LushRecipes.getInstance().getGuiHandler().guiBuilder();
        }

        if (title != null) {
            builder.title(title);
        }

        return builder
            .size(layer.getSize())
            .locked(true)
            .applyLayer(layer);
    }
}

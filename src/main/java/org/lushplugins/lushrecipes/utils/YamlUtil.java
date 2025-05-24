package org.lushplugins.lushrecipes.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushlib.gui.button.Button;
import org.lushplugins.lushlib.gui.button.SimpleItemButton;
import org.lushplugins.lushlib.gui.button.type.NextPageButton;
import org.lushplugins.lushlib.gui.button.type.PreviousPageButton;
import org.lushplugins.lushlib.gui.inventory.GuiBlueprint;
import org.lushplugins.lushlib.gui.inventory.GuiLayer;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushlib.utils.YamlUtils;
import org.lushplugins.lushrecipes.gui.button.*;

public class YamlUtil {

    // TODO: Replace with some form of Gui library
    public static GuiBlueprint getGuiBlueprint(@NotNull ConfigurationSection guiConfig) {
        GuiLayer layer = new GuiLayer(guiConfig.getStringList("format"));

        for (ConfigurationSection buttonSection : YamlUtils.getConfigurationSections(guiConfig, "buttons")) {
            DisplayItemStack item = org.lushplugins.lushlib.utils.converter.YamlConverter.getDisplayItem(buttonSection);

            char label = buttonSection.getName().charAt(0);
            String type = buttonSection.isString("type") ? buttonSection.getString("type") : switch (label) {
                case 'b' -> "back";
                case '<' -> "previous_page";
                case '>' -> "next_page";
                default -> null;
            };

            Button button = switch (type) {
                case "back" -> new MainMenuButton(item);
                case "previous_page" -> new PreviousPageButton(item);
                case "next_page" -> new NextPageButton(item);
                case null, default -> switch (label) {
                    case 'o', 'r' -> new UnknownButton(label, item);
                    default -> Character.isDigit(label) ? new UnknownButton(label, item) : new SimpleItemButton(item);
                };
            };

            layer.setButton(label, button);
        }

        return new GuiBlueprint(
            guiConfig.getString("title"),
            layer
        );
    }
}

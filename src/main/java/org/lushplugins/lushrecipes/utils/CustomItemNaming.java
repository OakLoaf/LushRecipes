package org.lushplugins.lushrecipes.utils;

import org.bukkit.Material;
import org.lushplugins.lushlib.utils.DisplayItemStack;

public class CustomItemNaming {

    public static DisplayItemStack apply(DisplayItemStack item) {
        if (!item.hasCustomModelData()) {
            return item;
        }

        switch (item.getType()) {
            case Material.APPLE -> {
                String displayName = null;
                switch (item.getCustomModelData()) {
                    case 1 -> displayName = "&rCherry";
                    case 2 -> displayName = "&rGrape";
                    case 3 -> displayName = "&rLemon";
                    case 4 -> displayName = "&rOrange";
                    case 5 -> displayName = "&rPeach";
                    case 6 -> displayName = "&rStrawberry";
                    case 7 -> displayName = "&rWither Apple";
                }

                if (displayName != null) {
                    item = DisplayItemStack.builder(item)
                        .setDisplayName(displayName)
                        .build();
                }
            }
            case Material.PAPER -> {
                if (item.getCustomModelData() == 101) {
                    item = DisplayItemStack.builder(item)
                        .setDisplayName("&rSeashell")
                        .build();
                }
            }
            case null, default -> {}
        }

        return item;
    }
}

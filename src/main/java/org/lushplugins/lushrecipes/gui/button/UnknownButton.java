package org.lushplugins.lushrecipes.gui.button;

import org.lushplugins.lushlib.gui.button.Button;
import org.lushplugins.lushlib.utils.DisplayItemStack;

public class UnknownButton extends Button {
    private final char label;
    private final DisplayItemStack item;

    public UnknownButton(char label, DisplayItemStack item) {
        this.label = label;
        this.item = item;
    }

    public char getLabel() {
        return label;
    }

    public DisplayItemStack getItem() {
        return item;
    }
}

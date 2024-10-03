package wtf.bhopper.nonsense.gui.font;

import java.awt.*;

public enum Fonts {

    ARIAL("Arial", 12, 18, 20, 22, 36, 72),
    SEGOE("Segoe UI", 16),
    CONSOLAS("Consolas", 11, 12, 16);

    public final String name;
    public final int[] sizes;

    Fonts(String name, int... sizes) {
        this.name = name;
        this.sizes = sizes;
    }

    public String getKey(int size) {
        return this.name + " " + size;
    }

    public Font load(int size) {
        return new Font(this.name, Font.PLAIN, size);
    }


}

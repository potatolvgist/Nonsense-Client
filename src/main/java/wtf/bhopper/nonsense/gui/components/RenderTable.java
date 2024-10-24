package wtf.bhopper.nonsense.gui.components;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import wtf.bhopper.nonsense.Nonsense;
import wtf.bhopper.nonsense.gui.font.Fonts;
import wtf.bhopper.nonsense.gui.font.TTFFontRenderer;
import wtf.bhopper.nonsense.util.misc.MathUtil;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

public class RenderTable<T> extends RenderComponent {

    private final int rowHeight;

    private final FontRenderer font;
    private final List<T> rows = new ArrayList<>();
    private final Class<T> clazz;
    private final Comparator<T> comparator;
    private final List<TableColumn> rowNames = new ArrayList<>();
    private int maxRows = 16;

    public RenderTable(Class<T> clazz, Comparator<T> comparator, String name, int x, int y) {
        super(name, x, y,0, 0);
        this.font = mc.bitFontRenderer;
        this.rowHeight = font.FONT_HEIGHT + 4;
        this.clazz = clazz;
        this.comparator = comparator;
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(TableColumn.class)) {
                rowNames.add(field.getAnnotation(TableColumn.class));
            }
        }
    }

    @Override
    public void draw(ScaledResolution res, float delta, int mouseX, int mouseY, boolean bypass) {

        List<String[]> displayRows = new ArrayList<>();

        int count = 0;
        for (T row : this.rows) {
            displayRows.add(getRow(row));
            ++count;
            if (count >= maxRows) {
                break;
            }
        }

        int[] lengths = new int[rowNames.size()];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = this.stringWidth(rowNames.get(i).value());
        }

        for (String[] displayRow : displayRows) {
            for (int i = 0; i < displayRow.length; i++) {
                int strWidth = this.stringWidth(displayRow[i]);
                if (strWidth > lengths[i]) {
                    lengths[i] = strWidth;
                }
            }
        }

        for (int i = 0; i < lengths.length; i++) {
            lengths[i] += 4;
        }

        this.setWidth(MathUtil.sumOf(lengths) + lengths.length * 4);
        this.setHeight(displayRows.size() * (rowHeight + 1));

        this.drawRect(0, 0, this.getWidth(), this.rowHeight, 0xAAAAAAAA);
        this.drawRect(0, this.rowHeight, this.getWidth(), this.getHeight() - this.rowHeight, 0xAA555555);

        int xOffset = 0;
        count = 0;
        for (TableColumn rowHeader : this.rowNames) {
            this.drawString(rowHeader.value(), xOffset + 2, 2, -1);
            xOffset += lengths[count];
            count++;
        }

        count = 0;
        for (String[] row : displayRows) {
            xOffset = 0;
            for (int i = 0; i < row.length; i++) {
                this.drawString(row[i], rowHeight * (count + 1) + 2, xOffset + 2, 0xFFAAAAAA);
                xOffset += lengths[i];
            }
            count++;
        }

    }

    @SafeVarargs
    public final void setRows(T... rows) {
        this.setRows(Arrays.asList(rows));
    }

    public void setRows(Collection<T> rows) {
        this.rows.clear();
        this.rows.addAll(rows);
        this.rows.sort(this.comparator);
    }

    public int stringWidth(String text) {
        return (int)(this.font.getStringWidth(text) * 2.0F);
    }

    public static <T> String[] getRow(T object) {
        List<String> fields = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TableColumn.class)) {
                try {
                    fields.add(String.valueOf(field.get(object)));
                } catch (IllegalArgumentException | IllegalAccessException ignored) {}
            }
        }

        return fields.toArray(new String[0]);
    }


    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TableColumn {
        String value();
        boolean count() default false;
    }
}

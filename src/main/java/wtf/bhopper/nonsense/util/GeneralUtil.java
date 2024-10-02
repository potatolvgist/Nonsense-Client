package wtf.bhopper.nonsense.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GeneralUtil {

    public static String capitalize(String str) {
        char prevChar = ' ';
        StringBuilder builder = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (prevChar == ' ') {
                builder.append(Character.toTitleCase(c));
            } else {
                builder.append(c);
            }
            prevChar = c;
        }

        return builder.toString();
    }

    public static int[] findIndexes(String search, String keyword) {
        List<Integer> indexes = new ArrayList<>();
        int index = search.indexOf(keyword);
        while (index != -1) {
            indexes.add(index);
            index = search.indexOf(keyword);
        }
        int[] result = new int[indexes.size()];
        for (int i = 0; i < indexes.size(); i++) {
            result[i] = indexes.get(i);
        }
        return result;
    }

    public static <T> T randomElement(Collection<T> collection) {
        return collection.stream()
                .skip((int) (collection.size() * Math.random()))
                .findFirst()
                .orElse(null);
    }

}

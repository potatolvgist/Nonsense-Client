package wtf.bhopper.nonsense.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import wtf.bhopper.nonsense.Nonsense;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonUtil {

    public static void getSafe(JsonObject object, String key, JsonSafeCallback callback) {
        JsonElement element = object.get(key);
        if (element != null) {
            try {
                callback.apply(element);
            } catch (Exception exception) {
                Nonsense.LOGGER.error(exception);
            }
        }
    }

    public static void writeToFile(JsonObject object, File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(object.toString());
        writer.close();
    }

    public static JsonObject readFromFile(File file) throws IOException {
        return new JsonParser().parse(new FileReader(file)).getAsJsonObject();
    }

    public interface JsonSafeCallback {
        void apply(JsonElement element);
    }

}

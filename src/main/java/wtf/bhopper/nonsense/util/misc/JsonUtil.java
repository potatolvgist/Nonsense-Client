package wtf.bhopper.nonsense.util.misc;

import com.google.gson.*;
import net.minecraft.nbt.*;
import wtf.bhopper.nonsense.Nonsense;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

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

    public static String nbtToJson(NBTTagCompound nbt) {
        return parseNbt(nbt).toString();
    }

    public static JsonObject parseNbt(NBTTagCompound nbt) {
        JsonObject object = new JsonObject();
        Set<String> keySet = nbt.getKeySet();

        for (String key : keySet) {
            switch (nbt.getTagId(key)) {
                case 1: // Byte
                    object.addProperty(key, nbt.getByte(key));
                    break;

                case 2: // Short
                    object.addProperty(key, nbt.getShort(key));
                    break;

                case 3: // Int
                    object.addProperty(key, nbt.getInteger(key));
                    break;

                case 4: // Long
                    object.addProperty(key, nbt.getLong(key));
                    break;

                case 5: // Float
                    object.addProperty(key, nbt.getFloat(key));
                    break;

                case 6: // Double
                    object.addProperty(key, nbt.getDouble(key));
                    break;

                case 7: // Byte Array
                    JsonArray byteArray = new JsonArray();
                    for (byte b : nbt.getByteArray(key)) {
                        byteArray.add(new JsonPrimitive(b));
                    }
                    object.add(key, byteArray);
                    break;

                case 8: // String
                    object.addProperty(key, nbt.getString(key));
                    break;

                case 9: // List
                    NBTTagList list = nbt.getTagListAny(key);
                    object.add(key, parseNbtList(list));
                    break;

                case 10: // Compound
                    JsonObject compound = parseNbt(nbt.getCompoundTag(key));
                    object.add(key, compound);
                    break;

                case 11: // Int Array
                    JsonArray intArray = new JsonArray();
                    for (int i : nbt.getIntArray(key)) {
                        intArray.add(new JsonPrimitive(i));
                    }
                    object.add(key, intArray);
                    break;

            }
        }

        return object;

    }

    public static JsonArray parseNbtList(NBTTagList list) {
        if (list.tagCount() == 0) {
            return new JsonArray();
        }

        JsonArray json = new JsonArray();
        byte id = list.get(0).getId();

        for (int i = 0; i < list.tagCount(); i++) {
            NBTBase tag = list.get(0);
            switch (id) {
                case 1: // Byte
                    json.add(new JsonPrimitive(((NBTTagByte)tag).getByte()));
                    break;

                case 2: // Short
                    json.add(new JsonPrimitive(((NBTTagShort)tag).getShort()));
                    break;

                case 3: // Int
                    json.add(new JsonPrimitive(((NBTTagInt)tag).getInt()));
                    break;

                case 4: // Long
                    json.add(new JsonPrimitive(((NBTTagLong)tag).getLong()));
                    break;

                case 5: // Float
                    json.add(new JsonPrimitive(((NBTTagFloat)tag).getFloat()));
                    break;

                case 6: // Double
                    json.add(new JsonPrimitive(((NBTTagDouble)tag).getDouble()));
                    break;

                case 7: // Byte Array
                    JsonArray byteArray = new JsonArray();
                    for (byte b : ((NBTTagByteArray)tag).getByteArray()) {
                        byteArray.add(new JsonPrimitive(b));
                    }
                    json.add(byteArray);
                    break;

                case 8: // String
                    json.add(new JsonPrimitive(((NBTTagString)tag).getString()));
                    break;

                case 9: // List
                    json.add(parseNbtList((NBTTagList)tag));
                    break;

                case 10: // Compound
                    json.add(parseNbt((NBTTagCompound)tag));
                    break;

                case 11: // Int Array
                    JsonArray intArray = new JsonArray();
                    for (int j : ((NBTTagIntArray)tag).getIntArray()) {
                        intArray.add(new JsonPrimitive(j));
                    }
                    json.add(intArray);
                    break;
            }
        }

        return json;
    }

    public interface JsonSafeCallback {
        void apply(JsonElement element);
    }

}

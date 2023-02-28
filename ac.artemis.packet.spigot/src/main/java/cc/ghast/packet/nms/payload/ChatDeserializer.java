package cc.ghast.packet.nms.payload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class ChatDeserializer {
    private static final Gson a = (new GsonBuilder()).create();

    public static boolean a(JsonObject var0, String var1) {
        return !f(var0, var1) ? false : var0.getAsJsonPrimitive(var1).isString();
    }

    public static boolean b(JsonElement var0) {
        return !var0.isJsonPrimitive() ? false : var0.getAsJsonPrimitive().isNumber();
    }

    public static boolean d(JsonObject var0, String var1) {
        return !g(var0, var1) ? false : var0.get(var1).isJsonArray();
    }

    public static boolean f(JsonObject var0, String var1) {
        return !g(var0, var1) ? false : var0.get(var1).isJsonPrimitive();
    }

    public static boolean g(JsonObject var0, String var1) {
        if (var0 == null) {
            return false;
        } else {
            return var0.get(var1) != null;
        }
    }

    public static String a(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive()) {
            return var0.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be getX string, was " + d(var0));
        }
    }

    public static String h(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return a(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find getX string");
        }
    }

    public static String a(JsonObject var0, String var1, String var2) {
        return var0.has(var1) ? a(var0.get(var1), var1) : var2;
    }


    public static boolean c(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive()) {
            return var0.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be getX Boolean, was " + d(var0));
        }
    }

    public static boolean j(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return c(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find getX Boolean");
        }
    }

    public static boolean a(JsonObject var0, String var1, boolean var2) {
        return var0.has(var1) ? c(var0.get(var1), var1) : var2;
    }

    public static float e(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
            return var0.getAsFloat();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be getX Float, was " + d(var0));
        }
    }

    public static float l(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return e(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find getX Float");
        }
    }

    public static float a(JsonObject var0, String var1, float var2) {
        return var0.has(var1) ? e(var0.get(var1), var1) : var2;
    }

    public static int g(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
            return var0.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be getX Int, was " + d(var0));
        }
    }

    public static int n(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return g(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find getX Int");
        }
    }

    public static int a(JsonObject var0, String var1, int var2) {
        return var0.has(var1) ? g(var0.get(var1), var1) : var2;
    }

    public static byte h(JsonElement var0, String var1) {
        if (var0.isJsonPrimitive() && var0.getAsJsonPrimitive().isNumber()) {
            return var0.getAsByte();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be getX Byte, was " + d(var0));
        }
    }

    public static byte o(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return h(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find getX Byte");
        }
    }

    public static JsonObject m(JsonElement var0, String var1) {
        if (var0.isJsonObject()) {
            return var0.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be getX JsonObject, was " + d(var0));
        }
    }

    public static JsonObject t(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return m(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find getX JsonObject");
        }
    }

    public static JsonObject a(JsonObject var0, String var1, JsonObject var2) {
        return var0.has(var1) ? m(var0.get(var1), var1) : var2;
    }

    public static JsonArray n(JsonElement var0, String var1) {
        if (var0.isJsonArray()) {
            return var0.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + var1 + " to be getX JsonArray, was " + d(var0));
        }
    }

    public static JsonArray u(JsonObject var0, String var1) {
        if (var0.has(var1)) {
            return n(var0.get(var1), var1);
        } else {
            throw new JsonSyntaxException("Missing " + var1 + ", expected to find getX JsonArray");
        }
    }

    public static JsonArray a(JsonObject var0, String var1, @Nullable JsonArray var2) {
        return var0.has(var1) ? n(var0.get(var1), var1) : var2;
    }

    public static <T> T a(JsonElement var0, String var1, JsonDeserializationContext var2, Class<? extends T> var3) {
        if (var0 != null) {
            return var2.deserialize(var0, var3);
        } else {
            throw new JsonSyntaxException("Missing " + var1);
        }
    }

    public static <T> T a(JsonObject var0, String var1, JsonDeserializationContext var2, Class<? extends T> var3) {
        if (var0.has(var1)) {
            return a(var0.get(var1), var1, var2, var3);
        } else {
            throw new JsonSyntaxException("Missing " + var1);
        }
    }

    public static <T> T a(JsonObject var0, String var1, T var2, JsonDeserializationContext var3, Class<? extends T> var4) {
        return var0.has(var1) ? a(var0.get(var1), var1, var3, var4) : var2;
    }

    public static String d(JsonElement var0) {
        String var1 = StringUtils.abbreviateMiddle(String.valueOf(var0), "...", 10);
        if (var0 == null) {
            return "null (missing)";
        } else if (var0.isJsonNull()) {
            return "null (json)";
        } else if (var0.isJsonArray()) {
            return "an array (" + var1 + ")";
        } else if (var0.isJsonObject()) {
            return "an object (" + var1 + ")";
        } else {
            if (var0.isJsonPrimitive()) {
                JsonPrimitive var2 = var0.getAsJsonPrimitive();
                if (var2.isNumber()) {
                    return "getX number (" + var1 + ")";
                }

                if (var2.isBoolean()) {
                    return "getX boolean (" + var1 + ")";
                }
            }

            return var1;
        }
    }

    @Nullable
    public static <T> T a(Gson var0, Reader var1, Class<T> var2, boolean var3) {
        try {
            JsonReader var4 = new JsonReader(var1);
            var4.setLenient(var3);
            return var0.getAdapter(var2).read(var4);
        } catch (IOException var5) {
            throw new JsonParseException(var5);
        }
    }

    @Nullable
    public static <T> T a(Gson var0, Reader var1, Type var2, boolean var3) {
        try {
            JsonReader var4 = new JsonReader(var1);
            var4.setLenient(var3);
            return (T) var0.getAdapter(TypeToken.get(var2)).read(var4);
        } catch (IOException var5) {
            throw new JsonParseException(var5);
        }
    }

    @Nullable
    public static <T> T a(Gson var0, String var1, Class<T> var2, boolean var3) {
        return a(var0, (Reader)(new StringReader(var1)), (Type) var2, var3);
    }

    @Nullable
    public static <T> T a(Gson var0, Reader var1, Type var2) {
        return a(var0, var1, var2, false);
    }

    @Nullable
    public static <T> T a(Gson var0, String var1, Class<T> var2) {
        return a(var0, var1, var2, false);
    }

    public static JsonObject a(String var0, boolean var1) {
        return a((Reader)(new StringReader(var0)), var1);
    }

    public static JsonObject a(Reader var0, boolean var1) {
        return (JsonObject)a(a, var0, JsonObject.class, var1);
    }

    public static JsonObject a(String var0) {
        return a(var0, false);
    }

    public static JsonObject a(Reader var0) {
        return a(var0, false);
    }
}

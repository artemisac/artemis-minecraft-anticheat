package cc.ghast.packet.nms.payload;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class MinecraftKey implements Comparable<MinecraftKey> {
    protected final String id;
    protected final String key;

    protected MinecraftKey(String[] var0) {
        this.id = var0[0] == null || var0[0].length() == 0 ? "minecraft" : var0[0];
        this.key = var0[1];


    }

    public MinecraftKey(String var0) {
        this(b(var0, ':'));
    }

    public MinecraftKey(String var0, String var1) {
        this(new String[]{var0, var1});
    }

    public static MinecraftKey a(String var0, char var1) {
        return new MinecraftKey(b(var0, var1));
    }

    @Nullable
    public static MinecraftKey a(String var0) {
        try {
            return new MinecraftKey(var0);
        } catch (ArtemisDigestException var2) {
            return null;
        }
    }

    protected static String[] b(String var0, char var1) {
        String[] var2 = new String[]{"minecraft", var0};
        int var3 = var0.indexOf(var1);
        if (var3 >= 0) {
            var2[1] = var0.substring(var3 + 1, var0.length());
            if (var3 >= 1) {
                var2[0] = var0.substring(0, var3);
            }
        }

        return var2;
    }

    public String getKey() {
        return this.key;
    }

    public String b() {
        return this.id;
    }

    public String toString() {
        return this.id + ':' + this.key;
    }

    public boolean equals(Object var0) {
        if (this == var0) {
            return true;
        } else if (!(var0 instanceof MinecraftKey)) {
            return false;
        } else {
            MinecraftKey var1 = (MinecraftKey)var0;
            return this.id.equals(var1.id) && this.key.equals(var1.key);
        }
    }

    public int hashCode() {
        return 31 * this.id.hashCode() + this.key.hashCode();
    }

    public int compareTo(MinecraftKey var0) {
        int var1 = this.key.compareTo(var0.key);
        if (var1 == 0) {
            var1 = this.id.compareTo(var0.id);
        }

        return var1;
    }

    public static MinecraftKey a(StringReader var0) throws ArtemisDigestException {
        int var1 = var0.getCursor();

        while(var0.canRead() && a(var0.peek())) {
            var0.skip();
        }

        String var2 = var0.getString().substring(var1, var0.getCursor());

        try {
            return new MinecraftKey(var2);
        } catch (ArtemisDigestException var4) {
            var0.setCursor(var1);
            throw new ArtemisDigestException(var4);
        }
    }

    public static boolean a(char var0) {
        return var0 >= '0' && var0 <= '9' || var0 >= 'a' && var0 <= 'z' || var0 == '_' || var0 == ':' || var0 == '/' || var0 == '.' || var0 == '-';
    }

    private static final Pattern pattern = Pattern.compile("[a-zA-Z0-9\t\n ./\\[\\]{}_+=|\\\\-]");

    private static boolean charMatch1(String var0) {
        return var0.chars().allMatch((var0x) -> var0x == 95 || var0x == 45 || var0x >= 97 && var0x <= 122 || var0x >= 48 && var0x <= 57 || var0x == 47 || var0x == 46);
    }

    private static boolean charMatch2(String var0) {
        return var0.chars().allMatch((var0x) -> var0x == 95 || var0x == 45 || var0x >= 97 && var0x <= 122 || var0x >= 48 && var0x <= 57 || var0x == 46);
    }


    public static class a implements JsonDeserializer<MinecraftKey>, JsonSerializer<MinecraftKey> {
        public a() {
        }

        public MinecraftKey deserialize(JsonElement var0, Type var1, JsonDeserializationContext var2) throws JsonParseException {
            return new MinecraftKey(ChatDeserializer.a(var0, "location"));
        }

        public JsonElement serialize(MinecraftKey var0, Type var1, JsonSerializationContext var2) {
            return new JsonPrimitive(var0.toString());
        }
    }
}

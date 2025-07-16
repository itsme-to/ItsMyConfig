package to.itsme.itsmyconfig.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtil {

    public static JsonElement findElement(final JsonObject object, final String... keys) {
        for (final String key : keys) {
            if (object.has(key)) {
                return object.get(key);
            }
        }
        return null;
    }

}

package to.itsme.itsmyconfig.placeholder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PlaceholderManager {

    private final Map<String, PlaceholderData> placeholders = Collections.synchronizedMap(new LinkedHashMap<>());

    public void register(final String key, final PlaceholderData value) {
        this.placeholders.put(key, value);
    }

    public void unregisterAll() {
        this.placeholders.clear();
    }

    public boolean has(final String key) {
        return this.placeholders.containsKey(key);
    }

    public PlaceholderData get(final String key) {
        return this.placeholders.get(key);
    }

    public Map<String, PlaceholderData> getPlaceholdersMap() {
        return placeholders;
    }

}

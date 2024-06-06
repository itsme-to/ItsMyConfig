package to.itsme.itsmyconfig.placeholder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The PlaceholderManager class is responsible for managing placeholders.
 * It provides methods to register, unregister, and retrieve placeholders.
 */
public final class PlaceholderManager {

    /**
     * Represents a synchronized map of placeholder keys and PlaceholderData objects.
     * Placeholders are used to represent dynamic values that can be replaced in messages or text.
     */
    private final Map<String, PlaceholderData> placeholders = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Registers a placeholder with the provided key and value.
     *
     * @param key   The key of the placeholder.
     * @param value The PlaceholderData object representing the value of the placeholder.
     */
    public void register(final String key, final PlaceholderData value) {
        this.placeholders.put(key, value);
    }

    /**
     * Clears all registered placeholders.
     */
    public void unregisterAll() {
        this.placeholders.clear();
    }

    /**
     * Unregisters a placeholder with the specified key.
     *
     * @param key The key of the placeholder to unregister.
     */
    public void unregister(final String key) {
        this.placeholders.remove(key);
    }

    /**
     * Checks if the specified key is present in the PlaceholderManager.
     *
     * @param key The key to check.
     * @return true if the key is present, false otherwise.
     */
    public boolean has(final String key) {
        return this.placeholders.containsKey(key);
    }

    /**
     * Retrieves the placeholder data object associated with the given key.
     *
     * @param key The key used to retrieve the placeholder data object.
     * @return The PlaceholderData object associated with the given key, or null if the key does not exist.
     */
    public PlaceholderData get(final String key) {
        return this.placeholders.get(key);
    }

    /**
     * Returns a {@link Map} of placeholders.
     *
     * @return a map containing placeholders as keys and their corresponding {@link PlaceholderData} objects as values
     */
    public Map<String, PlaceholderData> getPlaceholdersMap() {
        return placeholders;
    }

    /**
     * Retrieves the keys of all registered placeholders.
     *
     * @return a set containing the keys of all registered placeholders.
     */
    public Set<String> getPlaceholderKeys() {
        return placeholders.keySet();
    }
}

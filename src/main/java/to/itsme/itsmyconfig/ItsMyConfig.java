package to.itsme.itsmyconfig;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import to.itsme.itsmyconfig.command.CommandManager;
import to.itsme.itsmyconfig.listener.impl.PacketChatListener;
import to.itsme.itsmyconfig.listener.impl.PacketItemListener;
import to.itsme.itsmyconfig.placeholder.DynamicPlaceHolder;
import to.itsme.itsmyconfig.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.placeholder.PlaceholderManager;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.placeholder.type.AnimatedPlaceholderData;
import to.itsme.itsmyconfig.placeholder.type.ColorPlaceholderData;
import to.itsme.itsmyconfig.placeholder.type.RandomPlaceholderData;
import to.itsme.itsmyconfig.placeholder.type.StringPlaceholderData;
import to.itsme.itsmyconfig.progress.ProgressBar;
import to.itsme.itsmyconfig.progress.ProgressBarBucket;
import to.itsme.itsmyconfig.requirement.RequirementManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ItsMyConfig class represents the main configuration class for the plugin.
 * It extends the JavaPlugin class and provides methods to manage the plugin configuration.
 * It also holds instances of PlaceholderManager, ProgressBarBucket, RequirementManager, and BukkitAudiences.
 */
public final class ItsMyConfig extends JavaPlugin {

    private static final boolean ALLOW_ITEM_EDITS = false;

    private static ItsMyConfig instance;
    private final PlaceholderManager placeholderManager = new PlaceholderManager();
    private final ProgressBarBucket progressBarBucket = new ProgressBarBucket();
    private String symbolPrefix;
    private RequirementManager requirementManager;

    private BukkitAudiences adventure;

    /**
     * Gets the instance of ItsMyConfig.
     *
     * @return The instance of ItsMyConfig.
     */
    public static ItsMyConfig getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Loading ItsMyConfig...");
        final long start = System.currentTimeMillis();
        instance = this;
        new DynamicPlaceHolder(this, progressBarBucket).register();
        new CommandManager(this);

        this.requirementManager = new RequirementManager();
        this.adventure = BukkitAudiences.create(this);

        loadConfig();

        new Metrics(this, 21713);

        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketChatListener(this));

        if (ALLOW_ITEM_EDITS) {
            protocolManager.addPacketListener(new PacketItemListener(this));
        }

        this.getLogger().info("ItsMyConfig loaded in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        this.placeholderManager.unregisterAll();
        this.progressBarBucket.unregisterAll();
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    /**
     * The loadConfig method is responsible for loading the configuration file and initializing various settings and data.
     * It performs the following steps:
     * <p>
     * 1-2. Track previously registered placeholders and progress bars.
     * 3-4. Clear all registered placeholders and progress bars.
     * 5. Save the default configuration file if it does not exist
     * 6. Reload the configuration from the file.
     * 7. Loads the symbol prefix from the configuration.
     * 8-9. Maps to keep track of registered placeholders and progress bars to avoid duplicates.
     * 10-11-12. Lists to store log messages.
     * 13-14. Load and register placeholders and progress bars from the main configuration file.
     * 15. Load and register placeholders and progress bars from additional custom .yml files.
     * 16. Unregister non-existing placeholders and progress bars.
     * 17-18-19. Print all logs in the correct order.
     */
    public void loadConfig() {
        // cache old placeholder and bar names
        final Set<String> previousPlaceholders = new HashSet<>(placeholderManager.getPlaceholderKeys());
        final Set<String> previousProgressBars = new HashSet<>(progressBarBucket.getProgressBarKeys());

        // unregister all placeholders and bars
        this.placeholderManager.unregisterAll();
        this.progressBarBucket.unregisterAll();

        // load config.yml
        this.saveDefaultConfig();
        this.reloadConfig();
        this.loadSymbolPrefix();

        // load placeholders
        final Map<String, String> registeredPlaceholders = new HashMap<>();
        final Map<String, String> registeredProgressBars = new HashMap<>();
        final List<String> registeredLogs = new ArrayList<>();
        final List<String> duplicateLogs = new ArrayList<>();
        final List<String> unregisterLogs = new ArrayList<>();

        if (getConfig().isConfigurationSection("custom-placeholder")) {
            loadPlaceholdersSection(getConfig().getConfigurationSection("custom-placeholder"), "ItsMyConfig\\config.yml", registeredPlaceholders, registeredLogs, duplicateLogs, unregisterLogs);
        }
        if (getConfig().isConfigurationSection("custom-progress")) {
            loadProgressBarsSection(getConfig().getConfigurationSection("custom-progress"), "ItsMyConfig\\config.yml", registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
        }

        this.loadFolder(this.getDataFolder(), true, registeredPlaceholders, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);

        registeredLogs.forEach(this.getLogger()::info);
        duplicateLogs.forEach(this.getLogger()::warning);

        previousPlaceholders.removeAll(registeredPlaceholders.keySet());
        for (final String identifier : previousPlaceholders) {
            this.getLogger().info(String.format("Unregistering placeholder %s as it no longer exists in the configuration.", identifier));
        }

        previousProgressBars.removeAll(registeredProgressBars.keySet());
        for (final String identifier : previousProgressBars) {
            this.getLogger().info(String.format("Unregistering progress bar %s as it no longer exists in the configuration.", identifier));
        }

        unregisterLogs.forEach(this.getLogger()::info);

        // delete all logs from memory
        registeredPlaceholders.clear();
        registeredProgressBars.clear();
        registeredLogs.clear();
        duplicateLogs.clear();
        unregisterLogs.clear();
    }

    /**
     * Loads the symbol prefix from the configuration.
     */
    private void loadSymbolPrefix() {
        this.symbolPrefix = this.getConfig().getString("symbol-prefix");
    }

    /**
     * Recursively loads .yml files from the specified folder.
     * It iterates through the files in the folder, loading each .yml file using the `loadCustomYml` method if it meets the criteria.
     *
     * @param folder                  The folder from which to load .yml files.
     * @param registeredPlaceholders  A map of registered placeholders to avoid duplicates.
     * @param registeredProgressBars  A map of registered progress bars to avoid duplicates.
     * @param registeredLogs         The list to store registered log messages.
     * @param duplicateLogs          The list to store duplicate log messages.
     * @param unregisterLogs         The list to store unregister log messages.
     */
    private void loadFolder(
            final File folder,
            final boolean parent,
            final Map<String, String> registeredPlaceholders,
            final Map<String, String> registeredProgressBars,
            final List<String> registeredLogs,
            final List<String> duplicateLogs,
            final List<String> unregisterLogs
    ) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }

        final File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (final File file : files) {
            if (file.isDirectory()) {
                this.loadFolder(file, false, registeredPlaceholders, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
            } else if (file.isFile() && file.getName().endsWith(".yml") && !(parent && file.getName().equals("config.yml"))) {
                this.loadYAMLFile(file, registeredPlaceholders, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
            }
        }
    }

    /**
     * Loads custom data from a .yml file.
     * It reads the file using `YamlConfiguration` and extracts custom progress bars and placeholders if they exist.
     *
     * @param file                    The .yml file to load custom data from.
     * @param registeredPlaceholders  A map of registered placeholders to avoid duplicates.
     * @param registeredProgressBars  A map of registered progress bars to avoid duplicates.
     * @param registeredLogs         The list to store registered log messages.
     * @param duplicateLogs          The list to store duplicate log messages.
     * @param unregisterLogs         The list to store unregister log messages.
     */
    private void loadYAMLFile(
            final File file,
            final Map<String, String> registeredPlaceholders,
            final Map<String, String> registeredProgressBars,
            final List<String> registeredLogs,
            final List<String> duplicateLogs,
            final List<String> unregisterLogs
    ) {
        final String filePath = "ItsMyConfig\\" + file.getPath().replace("/", "\\").replace(getDataFolder().getPath() + "\\", "");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.isConfigurationSection("custom-placeholder")) {
            loadPlaceholdersSection(config.getConfigurationSection("custom-placeholder"), filePath, registeredPlaceholders, registeredLogs, duplicateLogs, unregisterLogs);
        }
        if (config.isConfigurationSection("custom-progress")) {
            loadProgressBarsSection(config.getConfigurationSection("custom-progress"), filePath, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
        }
    }

    /**
     * Loads custom progress bars from a YAML configuration section.
     * It iterates over each progress bar defined in the section, constructs a `ProgressBar` object, and registers it with the `progressBarBucket`.
     *
     * @param section                 The YAML configuration section containing progress bar data.
     * @param filePath                The path of the file from which the data is loaded.
     * @param registeredProgressBars  A map of registered progress bars to avoid duplicates.
     * @param registeredLogs         The list to store registered log messages.
     * @param duplicateLogs          The list to store duplicate log messages.
     * @param unregisterLogs         The list to store unregister log messages.
     */
    private void loadProgressBarsSection(
            final ConfigurationSection section,
            final String filePath,
            final Map<String, String> registeredProgressBars,
            final List<String> registeredLogs,
            final List<String> duplicateLogs,
            final List<String> unregisterLogs
    ) {
        for (final String identifier : section.getKeys(false)) {
            if (registeredProgressBars.containsKey(identifier)) {
                duplicateLogs.add(String.format("Duplicate progress bar '%s' found in files %s and %s. Unregistering.", identifier, formatPath(registeredProgressBars.get(identifier)), formatPath(filePath)));
                progressBarBucket.unregisterProgressBar(identifier);
                unregisterLogs.add(String.format("Unregistering progress bar %s due to duplication.", identifier));
            } else {
                final long currentTime = System.currentTimeMillis();
                final ConfigurationSection progressBarSection = section.getConfigurationSection(identifier);
                progressBarBucket.registerProgressBar(
                        new ProgressBar(
                                identifier,
                                progressBarSection.getString("symbol"),
                                progressBarSection.getString("completed-color"),
                                progressBarSection.getString("progress-color"),
                                progressBarSection.getString("remaining-color")
                        )
                );
                registeredProgressBars.put(identifier, filePath);
                registeredLogs.add(String.format("Registered progress bar %s from %s in %dms", identifier, formatPath(filePath), System.currentTimeMillis() - currentTime));
            }
        }
    }

    /**
     * Loads custom placeholders from a YAML configuration section.
     * It iterates over each placeholder defined in the section, constructs a corresponding `PlaceholderData` object, and registers it with the `placeholderManager`.
     * Additionally, it registers any associated requirements for each placeholder.
     *
     * @param section                 The YAML configuration section containing placeholder data.
     * @param filePath                The path of the file from which the data is loaded.
     * @param registeredPlaceholders  A map of registered placeholders to avoid duplicates.
     * @param registeredLogs         The list to store registered log messages.
     * @param duplicateLogs          The list to store duplicate log messages.
     * @param unregisterLogs         The list to store unregister log messages.
     */
    private void loadPlaceholdersSection(
            final ConfigurationSection section,
            final String filePath,
            final Map<String, String> registeredPlaceholders,
            final List<String> registeredLogs,
            final List<String> duplicateLogs,
            final List<String> unregisterLogs
    ) {
        if (section == null) {
            getLogger().warning(String.format("No custom placeholders found in file %s", formatPath(filePath)));
            return;
        }

        for (final String identifier : section.getKeys(false)) {
            if (registeredPlaceholders.containsKey(identifier)) {
                duplicateLogs.add(String.format("Duplicate placeholder '%s' found in files %s and %s. Unregistering.", identifier, formatPath(registeredPlaceholders.get(identifier)), formatPath(filePath)));
                placeholderManager.unregister(identifier);
                unregisterLogs.add(String.format("Unregistering placeholder %s due to duplication.", identifier));
            } else {
                final long currentTime = System.currentTimeMillis();
                final ConfigurationSection placeholderSection = section.getConfigurationSection(identifier);
                if (placeholderSection == null) {
                    getLogger().warning(String.format("Invalid placeholder configuration for %s in file %s", identifier, formatPath(filePath)));
                    continue;
                }

                // Use getPlaceholderData to retrieve PlaceholderData
                final PlaceholderData placeholderData = getPlaceholderData(placeholderSection);

                // Load requirements if they exist
                if (placeholderSection.isConfigurationSection("requirements")) {
                    final ConfigurationSection requirementsSection = placeholderSection.getConfigurationSection("requirements");
                    for (final String reqIdentifier : requirementsSection.getKeys(false)) {
                        final ConfigurationSection reqSection = requirementsSection.getConfigurationSection(reqIdentifier);
                        if (reqSection != null) {
                            placeholderData.registerRequirement(reqSection);
                        } else {
                            getLogger().warning(String.format("Invalid requirement configuration for %s in placeholder %s from file %s", reqIdentifier, identifier, formatPath(filePath)));
                        }
                    }
                }

                placeholderManager.register(identifier, placeholderData);
                registeredPlaceholders.put(identifier, filePath);
                registeredLogs.add(String.format("Registered placeholder %s from %s in %dms", identifier, formatPath(filePath), System.currentTimeMillis() - currentTime));
            }
        }
    }

    /**
     * Retrieves the placeholder data based on the provided configuration section and identifier.
     *
     * @param placeholderSection The configuration section containing the placeholder data.
     * @return The placeholder data object.
     */
    private PlaceholderData getPlaceholderData(final ConfigurationSection placeholderSection) {
        final PlaceholderType type = PlaceholderType.find(placeholderSection.getString("type"));

        final String valueProperty = "value";
        final String valuesProperty = "values";

        switch (type) {
            case RANDOM:
                return new RandomPlaceholderData(placeholderSection.getStringList(valuesProperty));
            case ANIMATION:
                return new AnimatedPlaceholderData(
                        placeholderSection.getStringList(valuesProperty),
                        placeholderSection.getInt("interval", 20)
                );
            case COLOR:
                return new ColorPlaceholderData(placeholderSection);
            default:
            case STRING:
                return new StringPlaceholderData(placeholderSection.getString(valueProperty, ""));
        }
    }

    /**
     * Formats a file path to start with "ItsMyConfig" and shortens it if it contains more than 5 directories.
     *
     * @param path The original file path.
     * @return The formatted file path.
     */
    private String formatPath(final String path) {
        final String[] parts = path.split(File.separator.equals("\\") ? "\\\\" : File.separator);
        if (parts.length > 5) {
            return parts[0] + "\\" + "..\\".repeat(parts.length - 3) + parts[parts.length - 2] + "\\" + parts[parts.length - 1];
        } else {
            return path;
        }
    }

    /**
     * Retrieves the instance of the `BukkitAudiences` class used for sending chat messages and titles.
     *
     * @return The instance of the `BukkitAudiences` class.
     * @throws IllegalStateException if the plugin is disabled and the `Adventure` instance is accessed.
     */
    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    /**
     * Retrieves the symbol prefix.
     *
     * @return The symbol prefix used in messages or text.
     */
    public String getSymbolPrefix() {
        return symbolPrefix;
    }

    /**
     * Retrieves the PlaceholderManager instance.
     *
     * @return The PlaceholderManager instance.
     */
    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    /**
     * Returns the RequirementManager object. The RequirementManager class is responsible for managing requirements
     * and validating them.
     *
     * @return the RequirementManager object
     */
    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

}

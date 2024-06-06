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
        this.progressBarBucket.clearAllProgressBars();
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
        Set<String> previousPlaceholders = new HashSet<>(placeholderManager.getPlaceholderKeys());
        Set<String> previousProgressBars = new HashSet<>(progressBarBucket.getProgressBarKeys());
        this.placeholderManager.unregisterAll();
        this.progressBarBucket.clearAllProgressBars();
        this.saveDefaultConfig();
        this.reloadConfig();
        this.loadSymbolPrefix();
        final Map<String, String> registeredPlaceholders = new HashMap<>();
        final Map<String, String> registeredProgressBars = new HashMap<>();
        List<String> registeredLogs = new ArrayList<>();
        List<String> duplicateLogs = new ArrayList<>();
        List<String> unregisterLogs = new ArrayList<>();
        this.loadPlaceholders(registeredPlaceholders, "ItsMyConfig\\config.yml", registeredLogs, duplicateLogs, unregisterLogs);
        this.loadProgressBars(registeredProgressBars, "ItsMyConfig\\config.yml", registeredLogs, duplicateLogs, unregisterLogs);
        this.loadCustomYmlFiles(registeredPlaceholders, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
        this.unregisterNonExistingPlaceholdersAndProgressBars(previousPlaceholders, registeredPlaceholders, previousProgressBars, registeredProgressBars, unregisterLogs);
        registeredLogs.forEach(this.getLogger()::info);
        duplicateLogs.forEach(this.getLogger()::warning);
        unregisterLogs.forEach(this.getLogger()::info);
    }

    /**
     * Loads the symbol prefix from the configuration.
     */
    private void loadSymbolPrefix() {
        this.symbolPrefix = this.getConfig().getString("symbol-prefix");
    }

    /**
     * Loads the placeholders from the configuration file and registers them with the placeholder manager.
     * This method iterates over the placeholders configuration section, retrieves the placeholder data,
     * registers any associated requirements, and finally registers the placeholder with the placeholder manager.
     *
     * @param registeredPlaceholders A map of registered placeholders to avoid duplicates.
     * @param currentFile            The current file being loaded.
     * @param registeredLogs         The list to store registered log messages.
     * @param duplicateLogs          The list to store duplicate log messages.
     * @param unregisterLogs         The list to store unregister log messages.
     */
    private void loadPlaceholders(final Map<String, String> registeredPlaceholders, final String currentFile, List<String> registeredLogs, List<String> duplicateLogs, List<String> unregisterLogs) {
        final ConfigurationSection placeholdersConfigSection = this.getConfig().getConfigurationSection("custom-placeholder");
        if (placeholdersConfigSection != null) {
            for (final String identifier : placeholdersConfigSection.getKeys(false)) {
                if (registeredPlaceholders.containsKey(identifier)) {
                    duplicateLogs.add(String.format("Duplicate placeholder '%s' detected in files %s and %s. Unregistering.", identifier, formatPath(registeredPlaceholders.get(identifier)), formatPath(currentFile)));
                    placeholderManager.unregister(identifier);
                    unregisterLogs.add(String.format("Unregistering placeholder %s due to duplication.", identifier));
                } else {
                    final long currentTime = System.currentTimeMillis();
                    final PlaceholderData data = this.getPlaceholderData(placeholdersConfigSection.getConfigurationSection(identifier));
                    registerPlaceholder(placeholdersConfigSection, identifier, data);
                    registeredPlaceholders.put(identifier, currentFile);
                    registeredLogs.add(String.format("Registered placeholder %s from %s in %dms", identifier, formatPath(currentFile), System.currentTimeMillis() - currentTime));
                }
            }
        }
    }

    /**
     * Loads progress bars from the configuration file.
     * Each progress bar is registered in the ProgressBarBucket.
     *
     * @param registeredProgressBars A map of registered progress bars to avoid duplicates.
     * @param currentFile            The current file being loaded.
     * @param registeredLogs         The list to store registered log messages.
     * @param duplicateLogs          The list to store duplicate log messages.
     * @param unregisterLogs         The list to store unregister log messages.
     */
    private void loadProgressBars(final Map<String, String> registeredProgressBars, final String currentFile, List<String> registeredLogs, List<String> duplicateLogs, List<String> unregisterLogs) {
        final ConfigurationSection progressBarConfigSection = this.getConfig().getConfigurationSection("custom-progress");
        if (progressBarConfigSection != null) {
            for (final String identifier : progressBarConfigSection.getKeys(false)) {
                if (registeredProgressBars.containsKey(identifier)) {
                    duplicateLogs.add(String.format("Duplicate progress bar '%s' detected in files %s and %s. Unregistering.", identifier, formatPath(registeredProgressBars.get(identifier)), formatPath(currentFile)));
                    progressBarBucket.unregisterProgressBar(identifier);
                    unregisterLogs.add(String.format("Unregistering progress bar %s due to duplication.", identifier));
                } else {
                    final long currentTime = System.currentTimeMillis();
                    final ConfigurationSection configurationSection = progressBarConfigSection.getConfigurationSection(identifier);
                    progressBarBucket.registerProgressBar(
                            new ProgressBar(
                                    identifier,
                                    configurationSection.getString("symbol"),
                                    configurationSection.getString("completed-color"),
                                    configurationSection.getString("progress-color"),
                                    configurationSection.getString("remaining-color")
                            )
                    );
                    registeredProgressBars.put(identifier, currentFile);
                    registeredLogs.add(String.format("Registered progress bar %s from %s in %dms", identifier, formatPath(currentFile), System.currentTimeMillis() - currentTime));
                }
            }
        }
    }

    /**
     * Loads custom .yml files from the plugin's data folder recursively.
     * It checks for the existence of the data folder, then proceeds to load .yml files using the `loadYmlFiles` method.
     *
     * @param registeredPlaceholders A map of registered placeholders to avoid duplicates.
     * @param registeredProgressBars A map of registered progress bars to avoid duplicates.
     * @param registeredLogs         The list to store registered log messages.
     * @param duplicateLogs          The list to store duplicate log messages.
     * @param unregisterLogs         The list to store unregister log messages.
     */
    private void loadCustomYmlFiles(final Map<String, String> registeredPlaceholders, final Map<String, String> registeredProgressBars, List<String> registeredLogs, List<String> duplicateLogs, List<String> unregisterLogs) {
        final File dataFolder = getDataFolder();
        if (dataFolder.exists()) {
            loadYmlFiles(dataFolder, registeredPlaceholders, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
        }
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
    private void loadYmlFiles(final File folder, final Map<String, String> registeredPlaceholders, final Map<String, String> registeredProgressBars, List<String> registeredLogs, List<String> duplicateLogs, List<String> unregisterLogs) {
        final File[] files = folder.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    loadYmlFiles(file, registeredPlaceholders, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
                } else if (file.isFile() && file.getName().endsWith(".yml") && !file.getName().equals("config.yml")) {
                    loadCustomYml(file, registeredPlaceholders, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
                }
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
    private void loadCustomYml(final File file, final Map<String, String> registeredPlaceholders, final Map<String, String> registeredProgressBars, List<String> registeredLogs, List<String> duplicateLogs, List<String> unregisterLogs) {
        final String filePath = "ItsMyConfig\\" + file.getPath().replace("/", "\\").replace(getDataFolder().getPath() + "\\", "");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.contains("custom-progress")) {
            loadProgressBarsFromYml(config.getConfigurationSection("custom-progress"), filePath, registeredProgressBars, registeredLogs, duplicateLogs, unregisterLogs);
        }
        if (config.contains("custom-placeholder")) {
            loadPlaceholdersFromYml(config.getConfigurationSection("custom-placeholder"), filePath, registeredPlaceholders, registeredLogs, duplicateLogs, unregisterLogs);
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
    private void loadProgressBarsFromYml(final ConfigurationSection section, final String filePath, final Map<String, String> registeredProgressBars, List<String> registeredLogs, List<String> duplicateLogs, List<String> unregisterLogs) {
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
    private void loadPlaceholdersFromYml(final ConfigurationSection section, final String filePath, final Map<String, String> registeredPlaceholders, List<String> registeredLogs, List<String> duplicateLogs, List<String> unregisterLogs) {
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
                    if (requirementsSection != null) {
                        for (final String reqIdentifier : requirementsSection.getKeys(false)) {
                            final ConfigurationSection reqSection = requirementsSection.getConfigurationSection(reqIdentifier);
                            if (reqSection != null) {
                                placeholderData.registerRequirement(reqSection);
                            } else {
                                getLogger().warning(String.format("Invalid requirement configuration for %s in placeholder %s from file %s", reqIdentifier, identifier, formatPath(filePath)));
                            }
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
     * Unregisters non-existing placeholders and progress bars and logs the actions.
     *
     * @param previousPlaceholders    A set of previously registered placeholders.
     * @param registeredPlaceholders  A map of currently registered placeholders to avoid duplicates.
     * @param previousProgressBars    A set of previously registered progress bars.
     * @param registeredProgressBars  A map of currently registered progress bars to avoid duplicates.
     * @param unregisterLogs          The list to store unregister log messages.
     */
    private void unregisterNonExistingPlaceholdersAndProgressBars(final Set<String> previousPlaceholders, final Map<String, String> registeredPlaceholders, final Set<String> previousProgressBars, final Map<String, String> registeredProgressBars, List<String> unregisterLogs) {
        // Unregister non-existing placeholders
        previousPlaceholders.removeAll(registeredPlaceholders.keySet());
        for (final String identifier : previousPlaceholders) {
            unregisterLogs.add(String.format("Unregistering placeholder %s as it no longer exists in the configuration.", identifier));
            placeholderManager.unregister(identifier);
        }

        // Unregister non-existing progress bars
        previousProgressBars.removeAll(registeredProgressBars.keySet());
        for (final String identifier : previousProgressBars) {
            unregisterLogs.add(String.format("Unregistering progress bar %s as it no longer exists in the configuration.", identifier));
            progressBarBucket.unregisterProgressBar(identifier);
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
     * Registers a placeholder with the provided identifier and data.
     *
     * @param placeholdersConfigSection The ConfigurationSection containing placeholder data.
     * @param identifier                The identifier of the placeholder.
     * @param data                      The PlaceholderData object representing the data of the placeholder.
     */
    private void registerPlaceholder(
            final ConfigurationSection placeholdersConfigSection,
            final String identifier,
            final PlaceholderData data
    ) {
        final ConfigurationSection requirementsConfigSection =
                placeholdersConfigSection.getConfigurationSection(identifier + ".requirements");
        if (requirementsConfigSection != null) {
            for (final String req : requirementsConfigSection.getKeys(false)) {
                data.registerRequirement(requirementsConfigSection.getConfigurationSection(req));
            }
        }
        this.placeholderManager.register(identifier, data);
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

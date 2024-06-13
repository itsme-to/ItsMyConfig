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
import to.itsme.itsmyconfig.placeholder.DynamicPlaceholder;
import to.itsme.itsmyconfig.placeholder.Placeholder;
import to.itsme.itsmyconfig.placeholder.PlaceholderManager;
import to.itsme.itsmyconfig.placeholder.PlaceholderType;
import to.itsme.itsmyconfig.placeholder.type.*;
import to.itsme.itsmyconfig.progress.ProgressBar;
import to.itsme.itsmyconfig.progress.ProgressBarBucket;
import to.itsme.itsmyconfig.requirement.RequirementManager;

import java.io.File;
import java.util.*;

/**
 * ItsMyConfig class represents the main configuration class for the plugin.
 * It extends the JavaPlugin class and provides methods to manage the plugin configuration.
 * It also holds instances of PlaceholderManager, ProgressBarBucket, RequirementManager, and BukkitAudiences.
 */
public final class ItsMyConfig extends JavaPlugin {

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
        new DynamicPlaceholder(this, progressBarBucket).register();
        new CommandManager(this);

        this.requirementManager = new RequirementManager();
        this.adventure = BukkitAudiences.create(this);

        this.loadConfig();

        new Metrics(this, 21713);

        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketChatListener(this));

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
     * 0. Cache time before loading placeholders.
     * 1-2. Track previously registered placeholders and progress bars.
     * 3-4. Clear all registered placeholders and progress bars.
     * 5. Save the default configuration file if it does not exist
     * 6. Reload the configuration from the file.
     * 7. Loads the symbol prefix from the configuration.
     * 8-9. Maps to keep track of registered placeholders and progress bars to avoid duplicates.
     * 10-11. Load and register placeholders and progress bars from the main configuration file.
     * 12. Load and register placeholders and progress bars from additional custom .yml files.
     * 13 - 14. Print all info about duplicated placeholders and bars.
     * 15 - 16. Print all info about deleted placeholders and bars.
     * 17. Clear maps from the cache to save memory.
     * 18. Send the placeholders loaded message.
     */
    public void loadConfig() {
        // 0: Cache time before loading placeholders
        final long time = System.currentTimeMillis();

        // 1 - 2: cache old placeholder and bar names
        final Set<String> previousPlaceholders = new HashSet<>(placeholderManager.getPlaceholderKeys());
        final Set<String> previousProgressBars = new HashSet<>(progressBarBucket.getProgressBarKeys());

        // 3 - 4: unregister all placeholders and bars
        this.placeholderManager.unregisterAll();
        this.progressBarBucket.unregisterAll();

        // 5 - 7: load config.yml
        this.saveDefaultConfig();
        this.reloadConfig();
        this.loadSymbolPrefix();

        // 8 - 9:  Maps to keep track of registered placeholders and progress bars
        final Map<String, List<String>> placeholderPaths = new HashMap<>();
        final Map<String, List<String>> progressBarPaths = new HashMap<>();

        // 10 - 11: Load and register placeholders and progress bars from the main configuration file
        if (getConfig().isConfigurationSection("custom-placeholder")) {
            loadPlaceholdersSection(getConfig().getConfigurationSection("custom-placeholder"), "ItsMyConfig\\config.yml", placeholderPaths);
        }
        if (getConfig().isConfigurationSection("custom-progress")) {
            loadProgressBarsSection(getConfig().getConfigurationSection("custom-progress"), "ItsMyConfig\\config.yml", progressBarPaths);
        }

        // 12: Load and register placeholders and progress bars from additional custom .yml files
        this.loadFolder(this.getDataFolder(), true, placeholderPaths, progressBarPaths);

        // 13 - 14: Print all info about duplicated placeholders and bars
        final String listSeparator = "\n   - ";
        final Comparator<String> comparator = Comparator.comparingInt(String::length);
        for (final Map.Entry<String, List<String>> entry : placeholderPaths.entrySet()) {
            final String name = entry.getKey();
            final List<String> paths = entry.getValue();

            paths.sort(comparator);
            if (paths.size() > 1) {
                this.getLogger().warning(
                        "Placeholder \"" + name + "\" is duplicated in the following files:" + listSeparator + String.join(listSeparator, paths)
                );
            }
        }

        for (final Map.Entry<String, List<String>> entry : progressBarPaths.entrySet()) {
            final String name = entry.getKey();
            final List<String> paths = entry.getValue();

            paths.sort(comparator);
            if (paths.size() > 1) {
                this.getLogger().warning("ProgressBar \"" + name + "\" is duplicated in the following files: \n" + String.join("\n  -", paths));
            }
        }

        // 15 - 16: Print all info about deleted placeholders and bars
        previousPlaceholders.removeAll(placeholderManager.getPlaceholderKeys());
        for (final String identifier : previousPlaceholders) {
            this.getLogger().info(String.format("Unregistering placeholder %s as it no longer exists in the configuration.", identifier));
        }

        previousProgressBars.removeAll(progressBarBucket.getProgressBarKeys());
        for (final String identifier : previousProgressBars) {
            this.getLogger().info(String.format("Unregistering progress bar %s as it no longer exists in the configuration.", identifier));
        }

        // 17: delete all cache from memory
        placeholderPaths.clear();
        progressBarPaths.clear();
        previousPlaceholders.clear();
        previousProgressBars.clear();

        // 18: Send the placeholders loaded message
        this.getLogger().info(
                 String.format(
                         "Loaded all %d Placeholders and %d ProgressBars in %dms",
                         placeholderManager.getPlaceholderKeys().size(),
                         progressBarBucket.getProgressBarKeys().size(),
                         System.currentTimeMillis() - time
                 )
        );
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
     * @param folder                 The folder from which to load .yml files.
     * @param placeholderPaths       A map of registered placeholders to avoid duplicates.
     * @param progressbarPaths       A map of registered progress bars to avoid duplicates.
     */
    private void loadFolder(
            final File folder,
            final boolean parent,
            final Map<String, List<String>> placeholderPaths,
            final Map<String, List<String>> progressbarPaths
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
                this.loadFolder(file, false, placeholderPaths, progressbarPaths);
            } else if (file.isFile() && file.getName().endsWith(".yml") && !(parent && file.getName().equals("config.yml"))) {
                this.loadYAMLFile(file, placeholderPaths, progressbarPaths);
            }
        }
    }

    /**
     * Loads custom data from a .yml file.
     * It reads the file using `YamlConfiguration` and extracts custom progress bars and placeholders if they exist.
     *
     * @param file                   The .yml file to load custom data from.
     * @param placeholderPaths       A map of registered placeholders to avoid duplicates.
     * @param progressbarPaths       A map of registered progress bars to avoid duplicates.
     */
    private void loadYAMLFile(
            final File file,
            final Map<String, List<String>> placeholderPaths,
            final Map<String, List<String>> progressbarPaths
    ) {
        final String filePath = "ItsMyConfig\\" + file.getPath().replace("/", "\\").replace(getDataFolder().getPath() + "\\", "");
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.isConfigurationSection("custom-placeholder")) {
            loadPlaceholdersSection(config.getConfigurationSection("custom-placeholder"), filePath, placeholderPaths);
        }
        if (config.isConfigurationSection("custom-progress")) {
            loadProgressBarsSection(config.getConfigurationSection("custom-progress"), filePath, progressbarPaths);
        }
    }

    /**
     * Loads custom progress bars from a YAML configuration section.
     * It iterates over each progress bar defined in the section, constructs a `ProgressBar` object, and registers it with the `progressBarBucket`.
     *
     * @param section                 The YAML configuration section containing progress bar data.
     * @param filePath                The path of the file from which the data is loaded.
     * @param paths                   A map of registered progress bars to avoid duplicates.
     */
    private void loadProgressBarsSection(
            final ConfigurationSection section,
            final String filePath,
            final Map<String, List<String>> paths
    ) {
        if (section == null) {
            getLogger().warning(String.format("No custom progressbars found in file %s", formatPath(filePath)));
            return;
        }

        for (final String identifier : section.getKeys(false)) {
            if (paths.containsKey(identifier)) {
                paths.get(identifier).add(formatPath(filePath));
                return;
            }

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
            paths.computeIfAbsent(identifier, v -> new ArrayList<>()).add(formatPath(filePath));
        }
    }

    /**
     * Loads custom placeholders from a YAML configuration section.
     * It iterates over each placeholder defined in the section, constructs a corresponding `PlaceholderData` object, and registers it with the `placeholderManager`.
     * Additionally, it registers any associated requirements for each placeholder.
     *
     * @param section                The YAML configuration section containing placeholder data.
     * @param filePath               The path of the file from which the data is loaded.
     * @param paths                  A map of registered placeholders to avoid duplicates.
     */
    private void loadPlaceholdersSection(
            final ConfigurationSection section,
            final String filePath,
            final Map<String, List<String>> paths
    ) {
        if (section == null) {
            getLogger().warning(String.format("No custom placeholders found in file %s", formatPath(filePath)));
            return;
        }

        for (final String identifier : section.getKeys(false)) {
            if (placeholderManager.has(identifier)) {
                paths.get(identifier).add(formatPath(filePath));
                return;
            }

            final ConfigurationSection placeholderSection = section.getConfigurationSection(identifier);
            if (placeholderSection == null) {
                getLogger().warning(String.format("Invalid placeholder configuration for %s in file %s", identifier, formatPath(filePath)));
                continue;
            }

            // Use getPlaceholderData to retrieve PlaceholderData
            final Placeholder placeholder = this.getPlaceholder(placeholderSection);

            // Load requirements if they exist
            if (placeholderSection.isConfigurationSection("requirements")) {
                final ConfigurationSection requirementsSection = placeholderSection.getConfigurationSection("requirements");
                for (final String reqIdentifier : requirementsSection.getKeys(false)) {
                    final ConfigurationSection reqSection = requirementsSection.getConfigurationSection(reqIdentifier);
                    if (reqSection != null) {
                        placeholder.registerRequirement(reqSection);
                    } else {
                        getLogger().warning(String.format("Invalid requirement configuration for %s in placeholder %s from file %s", reqIdentifier, identifier, formatPath(filePath)));
                    }
                }
            }

            placeholderManager.register(identifier, placeholder);
            paths.computeIfAbsent(identifier, v -> new ArrayList<>()).add(formatPath(filePath));
        }
    }

    /**
     * Retrieves the placeholder data based on the provided configuration section and identifier.
     *
     * @param placeholderSection The configuration section containing the placeholder data.
     * @return The placeholder data object.
     */
    private Placeholder getPlaceholder(final ConfigurationSection placeholderSection) {
        final PlaceholderType type = PlaceholderType.find(placeholderSection.getString("type"));

        final String valueProperty = "value";
        final String valuesProperty = "values";

        switch (type) {
            case RANDOM:
                return new RandomPlaceholder(placeholderSection.getStringList(valuesProperty));
            case LIST:
                return new ListPlaceholder(placeholderSection.getStringList(valuesProperty));
            case ANIMATION:
                return new AnimatedPlaceholder(
                        placeholderSection.getStringList(valuesProperty),
                        placeholderSection.getInt("interval", 20)
                );
            case COLOR:
                return new ColorPlaceholder(placeholderSection);
            case COLORED_TEXT:
                return new ColoredTextPlaceholder(placeholderSection.getString(valueProperty, ""));
            default:
            case STRING:
                return new StringPlaceholder(placeholderSection.getString(valueProperty, ""));
        }
    }

    /**
     * Formats a file path to start with "ItsMyConfig" and shortens it if it contains more than 5 directories.
     *
     * @param path The original file path.
     * @return The formatted file path.
     */
    private String formatPath(final String path) {
        final String separator = File.separator;
        final String normalizedPath = path.replace("/", separator).replace("\\", separator);
        final String[] parts = normalizedPath.split(separator.equals("\\") ? "\\\\" : separator);
        if (parts.length > 5) {
            final StringBuilder shortenedPath = new StringBuilder(parts[0]);
            shortenedPath.append(separator).append(parts[1]);
            for (int i = 2; i < parts.length - 2; i++) {
                shortenedPath.append(separator).append("..");
            }
            shortenedPath.append(separator).append(parts[parts.length - 2]).append(separator).append(parts[parts.length - 1]);
            return shortenedPath.toString();
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

package to.itsme.itsmyconfig;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.ConfigurationSection;
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

    /**
     * The loadConfig method is responsible for loading the configuration file and initializing various settings and data.
     * It performs the following steps:
     * <p>
     * 1. Clears all progress bars in the ProgressBarBucket.
     * 2. Saves the default configuration file if it does not exist.
     * 3. Reloads the configuration file.
     * 4. Loads the symbol prefix from the configuration.
     * 5. Loads the custom placeholders from the configuration and registers them.
     * 6. Loads the custom progress bars from the configuration and registers them.
     */
    public void loadConfig() {
        progressBarBucket.clearAllProgressBars();
        this.saveDefaultConfig();
        this.reloadConfig();
        this.loadSymbolPrefix();
        this.loadPlaceholders();
        this.loadProgressBars();
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
     */
    private void loadPlaceholders() {
        placeholderManager.unregisterAll();
        final ConfigurationSection placeholdersConfigSection =
                this.getConfig().getConfigurationSection("custom-placeholder");
        for (final String identifier : placeholdersConfigSection.getKeys(false)) {
            final long currentTime = System.currentTimeMillis();
            final PlaceholderData data = getPlaceholderData(placeholdersConfigSection, identifier);
            registerPlaceholder(placeholdersConfigSection, identifier, data);
            this.getLogger().info(String.format("Registered placeholder %s in %dms", identifier, System.currentTimeMillis() - currentTime));
        }
    }

    /**
     * Retrieves the placeholder data based on the provided configuration section and identifier.
     *
     * @param placeholdersConfigSection The configuration section containing the placeholder data.
     * @param identifier                The identifier of the placeholder.
     * @return The placeholder data object.
     */
    private PlaceholderData getPlaceholderData(ConfigurationSection placeholdersConfigSection, String identifier) {
        final String placeholderTypeProperty = identifier + ".type";
        final PlaceholderType type = PlaceholderType.find(placeholdersConfigSection.getString(placeholderTypeProperty));
        final String valuesProperty = identifier + ".values";
        final String valueProperty = identifier + ".value";

        switch (type) {
            case RANDOM:
                return new RandomPlaceholderData(placeholdersConfigSection.getStringList(valuesProperty));
            case ANIMATION:
                final int intervalPropertyDefaultValue = 20;
                return new AnimatedPlaceholderData(placeholdersConfigSection.getStringList(valuesProperty),
                        placeholdersConfigSection.getInt(identifier + ".interval", intervalPropertyDefaultValue));
            case COLOR:
                return new ColorPlaceholderData(placeholdersConfigSection.getConfigurationSection(identifier));
            default:
            case STRING:
                final String defaultValue = "";
                return new StringPlaceholderData(placeholdersConfigSection.getString(valueProperty, defaultValue));
        }
    }

    /**
     * Registers a placeholder with the provided identifier and data.
     *
     * @param placeholdersConfigSection The ConfigurationSection containing placeholder data.
     * @param identifier               The identifier of the placeholder.
     * @param data                     The PlaceholderData object representing the data of the placeholder.
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
     * Loads progress bars from the configuration file.
     * Each progress bar is registered in the ProgressBarBucket.
     */
    private void loadProgressBars() {
        final ConfigurationSection progressBarConfigSection =
                this.getConfig().getConfigurationSection("custom-progress");
        for (final String identifier : progressBarConfigSection.getKeys(false)) {
            final ConfigurationSection configurationSection =
                    progressBarConfigSection
                            .getConfigurationSection(identifier);
            progressBarBucket.registerProgressBar(
                    new ProgressBar(
                            identifier,
                            configurationSection.getString("symbol"),
                            configurationSection.getString("completed-color"),
                            configurationSection.getString("progress-color"),
                            configurationSection.getString("remaining-color")
                    )
            );
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

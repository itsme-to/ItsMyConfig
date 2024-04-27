package to.itsme.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import to.itsme.itsmyconfig.command.CommandManager;
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

public final class ItsMyConfig extends JavaPlugin {

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
        instance = this;
        new DynamicPlaceHolder(this, progressBarBucket).register();
        new CommandManager(this);

        this.requirementManager = new RequirementManager();
        this.adventure = BukkitAudiences.create(this);

        loadConfig();

        if (getConfig().getBoolean("bstats", true)) {
            new Metrics(this, 21713);
        }

        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketChatListener(this, PacketType.Play.Server.DISGUISED_CHAT, PacketType.Play.Server.SYSTEM_CHAT));
        protocolManager.addPacketListener(new PacketChatListener(this, PacketType.Play.Server.CHAT));
        protocolManager.addPacketListener(new PacketItemListener(this, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS));
    }

    public void loadConfig() {
        progressBarBucket.clearProgressBar();
        this.saveDefaultConfig();
        this.reloadConfig();

        this.symbolPrefix = this.getConfig().getString("symbol-prefix");
        placeholderManager.unregisterAll();
        final ConfigurationSection placeholdersSec = this.getConfig().getConfigurationSection("custom-placeholder");
        for (final String identifier : placeholdersSec.getKeys(false)) {
            final PlaceholderType type = PlaceholderType.find(placeholdersSec.getString(identifier + ".type"));
            final PlaceholderData data;
            switch (type) {
                case RANDOM:
                    data = new RandomPlaceholderData(placeholdersSec.getStringList(identifier + ".values"));
                    break;
                case ANIMATION:
                    data = new AnimatedPlaceholderData(
                            placeholdersSec.getStringList(identifier + ".values"),
                            placeholdersSec.getInt(identifier + ".interval", 20)
                    );
                    break;
                case COLOR:
                    data = new ColorPlaceholderData(
                            placeholdersSec.getConfigurationSection(identifier)
                    );
                    break;
                default:
                case STRING:
                    data = new StringPlaceholderData(placeholdersSec.getString(identifier + ".value", ""));
                    break;
            }

            final ConfigurationSection requirementSec = placeholdersSec.getConfigurationSection(identifier + ".requirements");
            if (requirementSec != null) {
                for (final String requirement : requirementSec.getKeys(false)) {
                    data.registerRequirement(requirementSec.getConfigurationSection(requirement));
                }
            }

            this.placeholderManager.register(identifier, data);
            this.getLogger().info(String.format("Registered placeholder %s", identifier));
        }

        final ConfigurationSection customProgressSec = this.getConfig().getConfigurationSection("custom-progress");
        for (final String identifier : customProgressSec.getKeys(false)) {
            final ConfigurationSection configurationSection = customProgressSec.getConfigurationSection(identifier);
            progressBarBucket.registerProgressBar(new ProgressBar(
                    identifier,
                    configurationSection.getString("symbol"),
                    configurationSection.getString("completed-color"),
                    configurationSection.getString("progress-color"),
                    configurationSection.getString("remaining-color")
            ));
        }

    }

    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public String getSymbolPrefix() {
        return symbolPrefix;
    }

    public PlaceholderManager getPlaceholderManager() {
        return placeholderManager;
    }

    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

}

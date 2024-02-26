package to.itsme.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import to.itsme.itsmyconfig.config.message.Message;
import to.itsme.itsmyconfig.config.message.MessageKey;
import to.itsme.itsmyconfig.requirement.RequirementManager;
import to.itsme.itsmyconfig.commands.ItsMyConfigCommandExecutor;
import to.itsme.itsmyconfig.commands.MessageCommandExecutor;
import to.itsme.itsmyconfig.config.DynamicPlaceHolder;
import to.itsme.itsmyconfig.config.placeholder.PlaceholderData;
import to.itsme.itsmyconfig.progress.ProgressBar;
import to.itsme.itsmyconfig.progress.ProgressBarBucket;

public class ItsMyConfig extends JavaPlugin {

    private static ItsMyConfig instance;
    private DynamicPlaceHolder dynamicPlaceHolder;
    private final ProgressBarBucket progressBarBucket = new ProgressBarBucket();
    private String symbolPrefix;
    private RequirementManager requirementManager;

    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        instance = this;
        this.dynamicPlaceHolder = new DynamicPlaceHolder(this, progressBarBucket);
        this.dynamicPlaceHolder.register();

        this.requirementManager = new RequirementManager();

        this.getCommand("itsmyconfig").setExecutor(new ItsMyConfigCommandExecutor(this));
        this.getCommand("message").setExecutor(new MessageCommandExecutor(this));

        this.adventure = BukkitAudiences.create(this);

        loadConfig();

        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketChatListener(this, PacketType.Play.Server.DISGUISED_CHAT, PacketType.Play.Server.SYSTEM_CHAT));
        protocolManager.addPacketListener(new PacketChatListener(this, PacketType.Play.Server.CHAT));
    }

    public void loadConfig() {
        progressBarBucket.clearProgressBar();
        this.saveDefaultConfig();
        this.reloadConfig();

        this.symbolPrefix = this.getConfig().getString("symbol-prefix");

        final ConfigurationSection customPlaceholderSection = this.getConfig().getConfigurationSection("custom-placeholder");
        for (final String identifier : customPlaceholderSection.getKeys(false)) {
            final String result = customPlaceholderSection.getString(identifier + ".value");
            final String type = customPlaceholderSection.getString(identifier + ".type");
            final PlaceholderData data = dynamicPlaceHolder.registerIdentifier(identifier, result, type);
            ConfigurationSection requirementSection = customPlaceholderSection.getConfigurationSection(identifier + ".requirements");
            if (requirementSection != null) {
                for (String requirement : requirementSection.getKeys(false)) {
                    data.registerRequirement(customPlaceholderSection
                            .getConfigurationSection(identifier + ".requirements." + requirement));
                }
            }

            this.getLogger().info(String.format("Registered placeholder %s", identifier));
        }

        ConfigurationSection messagesSection = this.getConfig().getConfigurationSection("messages");
        for (String identifier : messagesSection.getKeys(false)) {
            MessageKey messageKey = Message.getMessageKey(identifier);
            messageKey.setMessage(messagesSection.getStringList(identifier));
        }

        ConfigurationSection customProgressConfigurationSection = this.getConfig().getConfigurationSection("custom-progress");
        for (String identifier : customProgressConfigurationSection.getKeys(false)) {
            ConfigurationSection configurationSection = customProgressConfigurationSection.getConfigurationSection(identifier);
            String symbol = configurationSection.getString("symbol");
            String completedColor = configurationSection.getString("completed-color");
            String progressColor = configurationSection.getString("progress-color");
            String remainingColor = configurationSection.getString("remaining-color");
            ProgressBar progressBar = new ProgressBar(identifier, symbol, completedColor, progressColor, remainingColor);
            progressBarBucket.registerProgressBar(progressBar);
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

    public static ItsMyConfig getInstance() {
        return instance;
    }

    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

}

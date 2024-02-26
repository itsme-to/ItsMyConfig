package ua.realalpha.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ua.realalpha.itsmyconfig.commands.ItsMyConfigCommandExecutor;
import ua.realalpha.itsmyconfig.commands.MessageCommandExecutor;
import ua.realalpha.itsmyconfig.config.DynamicPlaceHolder;
import ua.realalpha.itsmyconfig.config.message.Message;
import ua.realalpha.itsmyconfig.config.message.MessageKey;
import ua.realalpha.itsmyconfig.config.placeholder.PlaceholderData;
import ua.realalpha.itsmyconfig.progress.ProgressBar;
import ua.realalpha.itsmyconfig.progress.ProgressBarBucket;
import ua.realalpha.itsmyconfig.requirement.RequirementManager;

import java.lang.reflect.Field;

public class ItsMyConfig extends JavaPlugin {


    private static ItsMyConfig instance;
    private DynamicPlaceHolder dynamicPlaceHolder;
    private final ProgressBarBucket progressBarBucket = new ProgressBarBucket();
    private String symbolPrefix;
    private RequirementManager requirementManager;

    private static final Field TEXT_COMPONENT_CONTENT;
    static {
        try {
            Class<?> textComponentImpClazz = Class.forName("net.kyori.adventure.text.TextComponentImpl");
            Field contentField = textComponentImpClazz.getDeclaredField("content");
            contentField.setAccessible(true);
            TEXT_COMPONENT_CONTENT = contentField;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private BukkitAudiences adventure;

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

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

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketChatListener(this, PacketType.Play.Server.DISGUISED_CHAT, PacketType.Play.Server.SYSTEM_CHAT));
        protocolManager.addPacketListener(new PacketChatListener(this, PacketType.Play.Server.CHAT));
    }

    public void loadConfig(){
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

    public String getSymbolPrefix() {
        return symbolPrefix;
    }

    public static void applyingChatColor(Component rootComponent){
        if (rootComponent instanceof TextComponent) {
            TextComponent textComponent = (TextComponent) rootComponent;
            String translateAlternateColorCodes = ChatColor.translateAlternateColorCodes('&', textComponent.content());
            modifyContentOfTextComponent(textComponent, translateAlternateColorCodes);
            for (Component component : rootComponent.children()) {
                applyingChatColor(component);
            }
        }
    }

    private static void modifyContentOfTextComponent(TextComponent textComponent, String content){
        try {
            TEXT_COMPONENT_CONTENT.set(textComponent, content);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItsMyConfig getInstance() {
        return instance;
    }

    public RequirementManager getRequirementManager() {
        return requirementManager;
    }

}

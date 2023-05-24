package ua.realalpha.itsmyconfig;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.utility.MinecraftVersion;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ua.realalpha.itsmyconfig.commands.MessageCommandExecutor;
import ua.realalpha.itsmyconfig.commands.OfflineCommandExecutor;
import ua.realalpha.itsmyconfig.commands.ReloadCommandExecutor;
import ua.realalpha.itsmyconfig.config.DynamicPlaceHolder;
import ua.realalpha.itsmyconfig.config.message.CommandUsage;
import ua.realalpha.itsmyconfig.config.message.Message;
import ua.realalpha.itsmyconfig.config.message.MessageKey;
import ua.realalpha.itsmyconfig.listeners.PlayerJoinListener;
import ua.realalpha.itsmyconfig.model.ActionBarModel;
import ua.realalpha.itsmyconfig.model.SubTitle;
import ua.realalpha.itsmyconfig.model.TitleModel;
import ua.realalpha.itsmyconfig.offlinecommand.OfflineCommandManager;
import ua.realalpha.itsmyconfig.progress.ProgressBar;
import ua.realalpha.itsmyconfig.progress.ProgressBarBucket;

import java.lang.reflect.Field;
import java.util.Collections;

public class ItsMyConfig extends JavaPlugin {


    private OfflineCommandManager offlineCommandManager;
    private ModelRepository modelRepository;
    private DynamicPlaceHolder dynamicPlaceHolder;
    private ProgressBarBucket progressBarBucket = new ProgressBarBucket();
    private String symbolPrefix;

    private static Field TEXT_COMPONENT_CONTENT;
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
        this.dynamicPlaceHolder = new DynamicPlaceHolder(progressBarBucket);
        this.dynamicPlaceHolder.register();

        this.getCommand("message").setExecutor(new MessageCommandExecutor(this));
        this.getCommand("itsmyconfig").setExecutor(new ReloadCommandExecutor(this));

        modelRepository = new ModelRepository();
        modelRepository.registerModel(new ActionBarModel(this));
        modelRepository.registerModel(new TitleModel(this));
        modelRepository.registerModel(new SubTitle(this));

        PluginManager pluginManager = this.getServer().getPluginManager();

        this.adventure = BukkitAudiences.create(this);

        loadConfig();


        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketChatListener(this, modelRepository));

        this.offlineCommandManager = new OfflineCommandManager(this);
        this.offlineCommandManager.read();

        PluginCommand offlineCommand = this.getCommand("offline");
        offlineCommand.setExecutor(new OfflineCommandExecutor(offlineCommandManager));

        pluginManager.registerEvents(new PlayerJoinListener(offlineCommandManager), this);


    }

    @Override
    public void onDisable() {
        this.offlineCommandManager.write();
    }

    public void loadConfig(){
        progressBarBucket.clearProgressBar();
        this.saveDefaultConfig();
        this.reloadConfig();



        this.symbolPrefix = this.getConfig().getString("symbol-prefix");

        ConfigurationSection customPlaceholderConfigurationSection = this.getConfig().getConfigurationSection("custom-placeholder");
        for (String identifier : customPlaceholderConfigurationSection.getKeys(false)) {
            String result = customPlaceholderConfigurationSection.getString(identifier + ".value");
            dynamicPlaceHolder.registerIdentifier(identifier, result);
            this.getLogger().info(String.format("Register placeHolder %s", identifier));
        }

        ConfigurationSection messagesConfigurationSection = this.getConfig().getConfigurationSection("messages");
        for (String identifier : messagesConfigurationSection.getKeys(false)) {
            MessageKey messageKey = Message.getMessageKey(identifier);
            messageKey.setMessage(messagesConfigurationSection.getStringList(identifier));
        }

        ConfigurationSection commandsConfigurationSection = this.getConfig().getConfigurationSection("commands");
        for (String identifier : commandsConfigurationSection.getKeys(false)) {
            MessageKey messageKey = CommandUsage.getMessageKey(identifier);
            messageKey.setMessage(Collections.singletonList(commandsConfigurationSection.getString(identifier + ".usage")));
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
        if(rootComponent instanceof TextComponent) {
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


}

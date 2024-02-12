package ua.realalpha.itsmyconfig.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.ItsMyConfig;
import ua.realalpha.itsmyconfig.config.message.CommandUsage;
import ua.realalpha.itsmyconfig.config.message.Message;
import ua.realalpha.itsmyconfig.config.message.MessageKey;
import ua.realalpha.itsmyconfig.xml.Tag;

import java.util.Collection;
import java.util.Collections;

public class ItsMyConfigCommandExecutor implements CommandExecutor {

    private final ItsMyConfig itsMyConfig;

    public ItsMyConfigCommandExecutor(ItsMyConfig itsMyConfig) {
        this.itsMyConfig = itsMyConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageKey.sendUsage(sender, CommandUsage.RELOAD);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                reload(sender, args);
                break;
            case "message":
                message(sender, args);
                break;
        }

        return false;
    }

    private void message(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("itsmyconfig.message")) {
            Message.NO_PERMISSION.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
            return;
        }

        if (args.length <= 2) {
            MessageKey.sendUsage(sender, CommandUsage.MESSAGE);
            return;
        }

        Collection<? extends Player> players;
        if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("*")){
            players = Bukkit.getOnlinePlayers();
        } else if (args[1].equalsIgnoreCase("me") && sender instanceof Player) {
            players = Collections.singletonList(((Player) sender));
        } else {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) return;
            players = Collections.singletonList(player);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if (i != args.length - 1) stringBuilder.append(' ');
        }

        players.forEach(player -> {
            String message = stringBuilder.toString();
            message = PlaceholderAPI.setPlaceholders(player, message);
            message = ChatColor.translateAlternateColorCodes('&', message);


            String[] strings = message.split("\\\\r?\\\\n|\\\\r");
            for (String string : strings) {
                String symbol = Tag.hasTagPresent(string) ? this.itsMyConfig.getSymbolPrefix() : "";
                player.sendMessage(symbol + string);
            }
        });

        if (sender instanceof Player) {
            Message.MESSAGE_SEND.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
        }
    }

    private void reload(CommandSender sender, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("itsmyconfig.reload")) {
            Message.NO_PERMISSION.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
            return;
        }

        itsMyConfig.loadConfig();
        Message.RELOAD.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
    }

}

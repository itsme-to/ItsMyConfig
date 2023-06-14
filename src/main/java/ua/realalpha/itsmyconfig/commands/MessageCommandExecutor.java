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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MessageCommandExecutor implements CommandExecutor {

    private ItsMyConfig itsMyConfig;

    public MessageCommandExecutor(ItsMyConfig itsMyConfig) {
        this.itsMyConfig = itsMyConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("itsmyconfig.message")) {
            Message.NO_PERMISSION.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
            return false;
        }

        if (args.length <= 1) {
            MessageKey.sendUsage(sender, CommandUsage.MESSAGE);
            return false;
        }

        Collection<? extends Player> players;
        if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("*")){
            players = Bukkit.getOnlinePlayers();
        } else if (args[0].equalsIgnoreCase("me") && sender instanceof Player) {
            players = Collections.singletonList(((Player) sender));
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) return false;
            players = Collections.singletonList(player);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if (i != args.length - 1) stringBuilder.append(' ');
        }

        players.forEach(player -> {
            String message = stringBuilder.toString();
            message = PlaceholderAPI.setPlaceholders(player, message);
            message = PlaceholderAPI.setBracketPlaceholders(player, message);
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

        return false;
    }
}

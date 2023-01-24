package ua.realalpha.itsmyconfig.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.ItsMyConfig;
import ua.realalpha.itsmyconfig.config.message.CommandUsage;
import ua.realalpha.itsmyconfig.config.message.Message;
import ua.realalpha.itsmyconfig.config.message.MessageKey;

public class ReloadCommandExecutor implements CommandExecutor {

    private ItsMyConfig itsMyConfig;

    public ReloadCommandExecutor(ItsMyConfig itsMyConfig) {
        this.itsMyConfig = itsMyConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")){
            MessageKey.sendUsage(sender, CommandUsage.RELOAD);
            return false;
        }

        if (sender instanceof Player && !sender.hasPermission("itsmyconfig.reload")) {
            Message.NO_PERMISSION.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
            return false;
        }
        itsMyConfig.loadConfig();
        Message.RELOAD.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
        return false;
    }
}

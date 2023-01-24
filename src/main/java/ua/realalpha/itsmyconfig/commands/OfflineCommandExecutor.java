package ua.realalpha.itsmyconfig.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.realalpha.itsmyconfig.config.message.CommandUsage;
import ua.realalpha.itsmyconfig.config.message.Message;
import ua.realalpha.itsmyconfig.config.message.MessageKey;
import ua.realalpha.itsmyconfig.offlinecommand.OfflineCommandEntry;
import ua.realalpha.itsmyconfig.offlinecommand.OfflineCommandManager;
import ua.realalpha.itsmyconfig.offlinecommand.OfflineCommandSender;

public class OfflineCommandExecutor implements CommandExecutor {

    private OfflineCommandManager offlineCommandManager;
    public OfflineCommandExecutor(OfflineCommandManager offlineCommandManager) {
        this.offlineCommandManager = offlineCommandManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("itsmyconfig.offline")) {
            Message.NO_PERMISSION.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
            return false;
        }
        if (args.length <= 2) {
            MessageKey.sendUsage(sender, CommandUsage.OFFLINE);
            return false;
        }

        OfflineCommandSender offlineCommandSender = OfflineCommandSender.valueOf(args[0].toUpperCase());

        if (offlineCommandSender == OfflineCommandSender.NONE){
            MessageKey.sendUsage(sender, CommandUsage.OFFLINE);
            return false;
        }

        String target = args[1];

        int delay;
        try {
            delay = Integer.parseInt(args[2]);
        }catch (NumberFormatException e){
            MessageKey.sendUsage(sender, CommandUsage.OFFLINE);
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if (i != args.length - 1) stringBuilder.append(' ');
        }

        OfflineCommandEntry offlineCommandEntry = new OfflineCommandEntry(offlineCommandSender, delay, stringBuilder.toString());

        Player player = Bukkit.getPlayerExact(target);
        if (player != null){
            offlineCommandEntry.execute(player, true);
        }else {
            offlineCommandManager.addOfflineCommandEntry(target, offlineCommandEntry);
        }
        Message.COMMAND_ADDED.getMessage().forEach(s -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\{player}", target)));

        return false;
    }

}

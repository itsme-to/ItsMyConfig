package ua.realalpha.itsmyconfig.offlinecommand;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ua.realalpha.itsmyconfig.ItsMyConfig;

public class OfflineCommandEntry {

    private final OfflineCommandSender offlineCommandSender;
    private final int delay;
    private final String command;

    public OfflineCommandEntry(OfflineCommandSender offlineCommandSender, int delay, String command) {
        this.offlineCommandSender = offlineCommandSender;
        this.delay = delay;
        this.command = command;
    }

    public void execute(Player player, boolean ignoreDelay){
        CommandSender commandSender = (offlineCommandSender == OfflineCommandSender.PLAYER ? player : Bukkit.getConsoleSender());
        Runnable runnable = () -> Bukkit.getServer().dispatchCommand(commandSender, command);

        if (delay == 0 || ignoreDelay){
            runnable.run();
        }else {
            Bukkit.getServer().getScheduler().runTaskLater(JavaPlugin.getPlugin(ItsMyConfig.class), runnable, delay);
        }


    }

    public OfflineCommandSender getOfflineCommandExecutor() {
        return offlineCommandSender;
    }

    public int getDelay() {
        return delay;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "OfflineCommandEntry{" +
                "offlineCommandSender=" + offlineCommandSender +
                ", delay=" + delay +
                ", command='" + command + '\'' +
                '}';
    }
}

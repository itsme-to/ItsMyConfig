package ua.realalpha.itsmyconfig.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ua.realalpha.itsmyconfig.offlinecommand.OfflineCommandManager;

public class PlayerJoinListener implements Listener {

    private OfflineCommandManager offlineCommandManager;

    public PlayerJoinListener(OfflineCommandManager offlineCommandManager) {
        this.offlineCommandManager = offlineCommandManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        offlineCommandManager.consumeOfflineCommandEntries(player);
    }

}

package ua.realalpha.itsmyconfig.offlinecommand;

import java.util.HashMap;
import java.util.Map;

public enum OfflineCommandSender {

    NONE, CONSOLE, PLAYER;

    private final static Map<String, OfflineCommandSender> OFFLINE_COMMAND_SENDER_BY_IDENTIFIER = new HashMap<>();

    static {
        for (OfflineCommandSender offlineCommandSender : OfflineCommandSender.values()) {
            OFFLINE_COMMAND_SENDER_BY_IDENTIFIER.put(offlineCommandSender.name(), offlineCommandSender);
        }
    }

    public static OfflineCommandSender getOfflineCommandSender(String identifier){
        return OFFLINE_COMMAND_SENDER_BY_IDENTIFIER.getOrDefault(identifier.toUpperCase(), OfflineCommandSender.NONE);
    }

}

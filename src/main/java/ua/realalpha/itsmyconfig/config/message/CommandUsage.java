package ua.realalpha.itsmyconfig.config.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CommandUsage {

    private final static Map<String, MessageKey> MESSAGE_KEY_BY_IDENTIFIER = new HashMap<>();

    public static MessageKey RELOAD = MessageKey.from("reload", "itsmyconfig <message/reload>");
    public static MessageKey MESSAGE = MessageKey.from("message", "itsmyconfig message <player> [message]");

    static {
        Field[] declaredFields = CommandUsage.class.getDeclaredFields();
        try {
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object o = field.get(null);
                if (o instanceof MessageKey) {
                    MessageKey messageKey = (MessageKey) o;
                    MESSAGE_KEY_BY_IDENTIFIER.put(messageKey.getKey(), messageKey);
                }
            }

        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static MessageKey getMessageKey(String identifier){
        return MESSAGE_KEY_BY_IDENTIFIER.get(identifier);
    }


}

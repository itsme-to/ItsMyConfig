package ua.realalpha.itsmyconfig.config.message;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Message {

    private static Map<String, MessageKey> MESSAGE_KEY_BY_IDENTIFIER = new HashMap<>();

    public static MessageKey RELOAD = MessageKey.from("reload");
    public static MessageKey NO_PERMISSION = MessageKey.from("nopermission");
    public static MessageKey MESSAGE_SEND = MessageKey.from("messageSend");
    public static MessageKey COMMAND_ADDED = MessageKey.from("commandAdded");
    public static MessageKey INVALID_USE = MessageKey.from("invalidUse");

    static {
        Field[] declaredFields = Message.class.getDeclaredFields();
        try {
            for (Field field : declaredFields) {
                Object o = field.get(null);
                if (o instanceof MessageKey) {
                    MessageKey messageKey = (MessageKey) o;
                    MESSAGE_KEY_BY_IDENTIFIER.put(messageKey.getKey(), messageKey);
                }
            }

        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static MessageKey getMessageKey(String identifier){
        return MESSAGE_KEY_BY_IDENTIFIER.get(identifier);
    }

}

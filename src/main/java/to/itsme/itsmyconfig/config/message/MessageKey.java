package to.itsme.itsmyconfig.config.message;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public interface MessageKey {

    String getKey();

    List<String> getMessage();

    void setMessage(List<String> message);

    static MessageKey from(String key, String message) {
        return new DefaultMessage(key, message);
    }

    static MessageKey from(String key) {
        return new DefaultMessage(key);
    }


    static void sendUsage(CommandSender commandSender, MessageKey messageKey){
        Message.INVALID_USE.getMessage().forEach(s -> {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\{usage}", String.join("", messageKey.getMessage())));
        });
    }

    class DefaultMessage implements MessageKey {

        private final String key;
        private List<String> message;

        public DefaultMessage(String key) {
            this.key = key;
        }

        public DefaultMessage(String key, String message) {
            this.key = key;
            this.message = Collections.singletonList(message);
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public List<String> getMessage() {
            return this.message;
        }

        @Override
        public void setMessage(List<String> message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "DefaultMessage{" +
                    "key='" + key + '\'' +
                    ", message=" + message +
                    '}';
        }
    }

}

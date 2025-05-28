package to.itsme.itsmyconfig.tag.impl.toast;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import to.itsme.itsmyconfig.ItsMyConfig;

import java.io.StringReader;

public class BukkitToastSender implements ToastSender {

    @Override
    public void sendToast(Player player, Component title, Component description, Material icon) {
        String titleJson = GsonComponentSerializer.gson().serialize(title);
        String descJson = GsonComponentSerializer.gson().serialize(description);

        // Use one advancement per player to avoid duplicates
        String id = "dynamic_toast_" + player.getUniqueId();
        NamespacedKey key = new NamespacedKey("itsmyconfig", id);

        // Remove any existing advancement with this key to avoid conflicts
        Bukkit.getUnsafe().removeAdvancement(key);

        String advancementJson = "{\n" +
                "  \"criteria\": {\n" +
                "    \"impossible\": {\n" +
                "      \"trigger\": \"minecraft:impossible\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"display\": {\n" +
                "    \"icon\": {\n" +
                "      \"item\": \"" + icon.getKey() + "\"\n" +
                "    },\n" +
                "    \"title\": " + titleJson + ",\n" +
                "    \"description\": " + descJson + ",\n" +
                "    \"frame\": \"goal\",\n" +
                "    \"announce_to_chat\": false,\n" +
                "    \"show_toast\": true,\n" +
                "    \"hidden\": true\n" +
                "  }\n" +
                "}";

        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, new StringReader(advancementJson));
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        progress.awardCriteria("impossible");

        Bukkit.getScheduler().runTaskLater(
            ItsMyConfig.getInstance(),
            () -> {
                player.getAdvancementProgress(advancement).revokeCriteria("impossible");
                Bukkit.getUnsafe().removeAdvancement(key);
            },
            20L
        );
    }
}

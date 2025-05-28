package to.itsme.itsmyconfig.tag.impl.toast;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import to.itsme.itsmyconfig.ItsMyConfig;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitToastSender implements ToastSender {

    private static final Map<NamespacedKey, Long> advancementCleanupQueue = new ConcurrentHashMap<>();
    private static final long EXPIRY_MS = 5 * 60 * 1000; // 5 minutes

    static {
        // Schedule cleanup every minute
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                advancementCleanupQueue.entrySet().removeIf(entry -> {
                    if (now - entry.getValue() > EXPIRY_MS) {
                        Bukkit.getUnsafe().removeAdvancement(entry.getKey());
                        return true;
                    }
                    return false;
                });

                // Reload data if needed
                if (!advancementCleanupQueue.isEmpty()) {
                    Bukkit.reloadData();
                }
            }
        }.runTaskTimer(ItsMyConfig.getInstance(), 20 * 60, 20 * 60); // 60s interval
    }

    @Override
    public void sendToast(Player player, Component title, Component description, Material icon) {
        String titleJson = GsonComponentSerializer.gson().serialize(title);
        String descJson = GsonComponentSerializer.gson().serialize(description);

        // Generate unique key per send to avoid cache conflicts
        NamespacedKey key = new NamespacedKey("itsmyconfig", "toast_" + UUID.randomUUID());

        String advancementJson = "{\n" +
                "  \"criteria\": {\n" +
                "    \"impossible\": {\n" +
                "      \"trigger\": \"minecraft:impossible\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"display\": {\n" +
                "    \"icon\": {\n" +
                "      \"item\": \"" + icon.getKey() + "\",\n" +
                "      \"id\": \"" + icon.getKey() + "\"\n" +
                "    },\n" +
                "    \"title\": " + titleJson + ",\n" +
                "    \"description\": " + descJson + ",\n" +
                "    \"frame\": \"goal\",\n" +
                "    \"announce_to_chat\": false,\n" +
                "    \"show_toast\": true,\n" +
                "    \"hidden\": true\n" +
                "  }\n" +
                "}";

        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, advancementJson);
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        progress.awardCriteria("impossible");

        // Schedule revoke + cleanup
        Bukkit.getScheduler().runTaskLater(
                ItsMyConfig.getInstance(),
                () -> {
                    player.getAdvancementProgress(advancement).revokeCriteria("impossible");
                    advancementCleanupQueue.put(key, System.currentTimeMillis());
                },
                20L // 1 second delay
        );
    }
}

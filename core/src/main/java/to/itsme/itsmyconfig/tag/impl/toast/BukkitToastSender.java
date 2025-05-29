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
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                boolean removedAny = advancementCleanupQueue.entrySet().removeIf(entry -> {
                    if (now - entry.getValue() > EXPIRY_MS) {
                        Bukkit.getUnsafe().removeAdvancement(entry.getKey());
                        return true;
                    }
                    return false;
                });

                if (removedAny) {
                    Bukkit.reloadData();
                }
            }
        }.runTaskTimer(ItsMyConfig.getInstance(), 20 * 60, 20 * 60); // every 60s
    }

    /**
     * Sends a custom toast message to the player.
     *
     * @param player     The player to receive the toast.
     * @param title      The title component of the toast.
     * @param icon       The material to show as the icon.
     * @param frameType  The frame type: "task", "goal", or "challenge".
     */
    public void sendToast(Player player, Component title, Material icon, String frameType) {
        String titleJson = GsonComponentSerializer.gson().serialize(title);
        String descJson = GsonComponentSerializer.gson().serialize(Component.empty());

        String sanitizedFrame = switch (frameType.toLowerCase()) {
            case "challenge", "goal" -> frameType.toLowerCase();
            default -> "task";
        };

        NamespacedKey key = new NamespacedKey("itsmyconfig", "toast_" + UUID.randomUUID());
        String advancementJson = String.format(
            """
            {
              "criteria": {
                "impossible": {
                  "trigger": "minecraft:impossible"
                }
              },
              "display": {
                "icon": {
                  "item": "%s",
                  "id": "%s"
                },
                "title": %s,
                "description": %s,
                "frame": "%s",
                "announce_to_chat": false,
                "show_toast": true,
                "hidden": true
              }
            }
            """,
            icon.getKey(),       // item
            icon.getKey(),       // id
            titleJson,           // serialized Component
            descJson,            // empty component
            sanitizedFrame       // "task", "goal", "challenge"
        );

        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, advancementJson);
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        progress.awardCriteria("impossible");

        Bukkit.getScheduler().runTaskLater(
                ItsMyConfig.getInstance(),
                () -> {
                    player.getAdvancementProgress(advancement).revokeCriteria("impossible");
                    advancementCleanupQueue.put(key, System.currentTimeMillis());
                },
                20L // 1 second later
        );
    }
}

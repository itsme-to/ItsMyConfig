package to.itsme.itsmyconfig;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class PacketItemListener extends PacketAdapter {

    private final ItsMyConfig plugin;
    private final Pattern colorSymbolPattern;
    private final Pattern symbolPrefixPattern;
    private final Pattern tagPattern = Pattern.compile("<(?:\\\\.|[^<>])*>");
    private final GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();

    public PacketItemListener(
            final ItsMyConfig plugin,
            final PacketType... types
    ) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
        this.colorSymbolPattern = Pattern.compile(Pattern.quote("§"));
        this.symbolPrefixPattern = Pattern.compile(Pattern.quote(plugin.getSymbolPrefix()));
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        final PacketContainer packetContainer = event.getPacket();
        final Player player = event.getPlayer();

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            final StructureModifier<ItemStack> itemModifier = packetContainer.getItemModifier();
            final ItemStack itemStack = itemModifier.readSafely(0);
            processItem(itemStack, player);
            itemModifier.write(0, itemStack);
        } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            final StructureModifier<List<ItemStack>> itemArrayModifier = packetContainer.getItemListModifier();
            final List<ItemStack> itemStacks = itemArrayModifier.readSafely(0);
            itemStacks.forEach(itemStack -> processItem(itemStack, player));
            itemArrayModifier.write(0, itemStacks);
        }
    }

    private void processItem(ItemStack itemStack, Player player) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final NbtCompound itemNbt = (NbtCompound) NbtFactory.fromItemTag(itemStack);

        if (itemNbt == null || !itemNbt.containsKey("display")) {
            return;
        }

        final NbtCompound displayNbt = itemNbt.getCompound("display");

        if (displayNbt.containsKey("Name")) {
            BaseComponent[] components = ComponentSerializer.parse(displayNbt.getString("Name"));
            TextComponent textComponent = new TextComponent(components);
            String plainText = textComponent.toLegacyText();

            if (startsWithSymbol(plainText)) {
                Component translatedComponent = Utilities.translate(processMessage(plainText), player)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                Utilities.applyChatColors(translatedComponent);
                displayNbt.put("Name", gsonComponentSerializer.serialize(translatedComponent));
            }
        }

        if (displayNbt.containsKey("Lore")) {
            final NbtList<String> loreNbt = displayNbt.getList("Lore");

            if (loreNbt == null || loreNbt.size() == 0) {
                return;
            }

            final List<String> processedLore = new ArrayList<>();
            for (final String loreLine : loreNbt) {
                BaseComponent[] components = ComponentSerializer.parse(loreLine);
                TextComponent textComponent = new TextComponent(components);
                String plainText = textComponent.toLegacyText();

                if (startsWithSymbol(plainText)) {
                    Component translatedComponent = Utilities.translate(processMessage(plainText), player)
                            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
                    Utilities.applyChatColors(translatedComponent);
                    processedLore.add(gsonComponentSerializer.serialize(translatedComponent));
                } else {
                    processedLore.add(loreLine);
                }
            }

            final NbtList<String> newLoreNbt = NbtFactory.ofList("Lore");
            processedLore.stream()
                    .filter(loreText -> !loreText.isEmpty())
                    .map(loreText -> ChatColor.translateAlternateColorCodes('&', loreText))
                    .forEach(newLoreNbt::add);

            displayNbt.put("Lore", newLoreNbt);
        }

        itemNbt.put("display", displayNbt);
        NbtFactory.setItemTag(itemStack, itemNbt);
    }

    private boolean startsWithSymbol(final String message) {
        return message != null && !message.isEmpty() &&
                tagPattern.matcher(Utilities.colorless(message)).replaceAll("").trim().startsWith(plugin.getSymbolPrefix());
    }

    private String processMessage(final String message) {
        return colorSymbolPattern.matcher(symbolPrefixPattern.matcher(message).replaceFirst("")).replaceAll("&");
    }
}
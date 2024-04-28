package to.itsme.itsmyconfig.listener.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import to.itsme.itsmyconfig.ItsMyConfig;
import to.itsme.itsmyconfig.component.AbstractComponent;
import to.itsme.itsmyconfig.component.impl.TextfulComponent;
import to.itsme.itsmyconfig.listener.PacketListener;
import to.itsme.itsmyconfig.util.Utilities;

import java.util.ArrayList;
import java.util.List;

public final class PacketItemListener extends PacketListener {

    private final GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();

    public PacketItemListener(
            final ItsMyConfig plugin
    ) {
        super(plugin, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
    }

    @Override
    public void onPacketSending(final PacketEvent event) {
        final PacketContainer packetContainer = event.getPacket();
        final Player player = event.getPlayer();

        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            final StructureModifier<ItemStack> itemModifier = packetContainer.getItemModifier();
            final ItemStack itemStack = itemModifier.readSafely(0);
            this.processItem(itemStack, player);
            itemModifier.write(0, itemStack);
        } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            final StructureModifier<List<ItemStack>> itemArrayModifier = packetContainer.getItemListModifier();
            final List<ItemStack> itemStacks = itemArrayModifier.readSafely(0);
            if (itemStacks != null && !itemStacks.isEmpty()) {
                itemStacks.forEach(itemStack -> this.processItem(itemStack, player));
                itemArrayModifier.write(0, itemStacks);
            }

            final StructureModifier<ItemStack> itemModifier = packetContainer.getItemModifier();
            final ItemStack itemStack = itemModifier.readSafely(0);
            if (itemStack != null) {
                this.processItem(itemStack, player);
                itemModifier.write(0, itemStack);
            }
        }
    }

    private void processItem(final ItemStack itemStack, final Player player) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        final NbtCompound itemNbt = (NbtCompound) NbtFactory.fromItemTag(itemStack);
        if (itemNbt == null || !itemNbt.containsKey("display")) {
            return;
        }

        final NbtCompound displayNbt = itemNbt.getCompound("display");
        if (displayNbt.containsKey("Name")) {
            final AbstractComponent component = AbstractComponent.parse(displayNbt.getString("Name"));
            if (component instanceof TextfulComponent) {
                ((TextfulComponent) component).forceUnitalic = true;
            }
            final String text = component.toMiniMessage();
            if (this.startsWithSymbol(text)) {
                final Component translatedComponent = Utilities.translate(processMessage(text), player);
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
                final AbstractComponent component = AbstractComponent.parse(loreLine);
                if (component instanceof TextfulComponent) {
                    ((TextfulComponent) component).forceUnitalic = true;
                }
                final String text = component.toMiniMessage();
                if (this.startsWithSymbol(text)) {
                    final Component translatedComponent = Utilities.translate(processMessage(text), player);
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

}
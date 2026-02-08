package me.green.tpa.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import me.green.tpa.GreenTPA;

public class ChatUtil {

    private final GreenTPA plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ChatUtil(GreenTPA plugin) {
        this.plugin = plugin;
    }

    public Component parse(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs of key and value");
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }

        // Support legacy color codes
        message = message.replace("&", "§");

        if (!plugin.getConfig().getBoolean("settings.clickable-chat", true)) {
             return MiniMessage.builder()
                .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .resolver(StandardTags.gradient())
                    .resolver(StandardTags.rainbow())
                    .resolver(StandardTags.newline())
                    .build())
                .build()
                .deserialize(miniMessage.serialize(LegacyComponentSerializer.legacySection().deserialize(message)));
        }

        return miniMessage.deserialize(miniMessage.serialize(LegacyComponentSerializer.legacySection().deserialize(message)));
    }

    public void sendMessage(CommandSender sender, String messageKey, String... placeholders) {
        String prefix = plugin.getConfig().getString("settings.prefix", "<green>[GreenTPA]</green> ");
        String message = plugin.getMessagesConfig().getString("messages." + messageKey);
        if (message == null) {
            sender.sendMessage("Message key " + messageKey + " not found!");
            return;
        }
        sender.sendMessage(parse(prefix + message, placeholders));
    }

    public void sendRawMessage(CommandSender sender, String rawMessage) {
        sender.sendMessage(miniMessage.deserialize(rawMessage));
    }
}

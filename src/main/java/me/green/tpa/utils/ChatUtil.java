package me.green.tpa.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.command.CommandSender;
import me.green.tpa.GreenTPA;

public class ChatUtil {

    private final GreenTPA plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ChatUtil(GreenTPA plugin) {
        this.plugin = plugin;
    }

    private String legacyToMiniMessage(String message) {
        if (message == null) return null;

        // Handle both & and §
        String[] legacy = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "k", "l", "m", "n", "o", "r"};
        String[] tags = {"<black>", "<dark_blue>", "<dark_green>", "<dark_aqua>", "<dark_red>", "<dark_purple>", "<gold>", "<gray>", "<dark_gray>", "<blue>", "<green>", "<aqua>", "<red>", "<light_purple>", "<yellow>", "<white>", "<obfuscated>", "<bold>", "<strikethrough>", "<underlined>", "<italic>", "<reset>"};

        for (int i = 0; i < legacy.length; i++) {
            message = message.replace("&" + legacy[i], tags[i]);
            message = message.replace("§" + legacy[i], tags[i]);
        }

        return message;
    }

    public String replacePlaceholders(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs of key and value");
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return message;
    }

    public Component parse(String message, String... placeholders) {
        if (placeholders.length % 2 != 0) {
            throw new IllegalArgumentException("Placeholders must be in pairs of key and value");
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }

        // Support both legacy color codes and MiniMessage tags
        message = legacyToMiniMessage(message);

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
                .deserialize(message);
        }

        return miniMessage.deserialize(message);
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
        sender.sendMessage(miniMessage.deserialize(legacyToMiniMessage(rawMessage)));
    }
}

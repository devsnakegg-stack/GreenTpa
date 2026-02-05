package me.green.tpa;

import me.green.tpa.commands.*;
import me.green.tpa.listeners.DeathListener;
import me.green.tpa.listeners.MoveListener;
import me.green.tpa.manager.*;
import me.green.tpa.utils.ChatUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class GreenTPA extends JavaPlugin {

    private ChatUtil chatUtil;
    private RequestManager requestManager;
    private CooldownManager cooldownManager;
    private ToggleManager toggleManager;
    private TeleportManager teleportManager;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private File dataFile;

    @Override
    public void onDisable() {
        if (toggleManager != null && dataFile != null) {
            toggleManager.save(dataFile);
        }
        getLogger().info("GreenTPA has been disabled!");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createMessagesConfig();

        this.chatUtil = new ChatUtil(this);
        this.requestManager = new RequestManager();
        this.cooldownManager = new CooldownManager(getConfig().getInt("settings.cooldown-time", 30));
        this.toggleManager = new ToggleManager();
        this.toggleManager.setDefaultAutoAccept(getConfig().getBoolean("settings.auto-accept-default", false));
        this.dataFile = new File(getDataFolder(), "data.yml");
        this.toggleManager.load(this.dataFile);
        this.teleportManager = new TeleportManager(this);

        registerCommands();
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        getLogger().info("GreenTPA has been enabled!");
    }

    private void registerCommands() {
        TPACommand tpaCommand = new TPACommand(this);
        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpahere").setExecutor(tpaCommand);
        getCommand("tpaccept").setExecutor(tpaCommand);
        getCommand("tpdeny").setExecutor(tpaCommand);
        getCommand("tpcancel").setExecutor(tpaCommand);
        getCommand("tpalist").setExecutor(tpaCommand);

        ToggleCommands toggleCommands = new ToggleCommands(this);
        getCommand("tptoggle").setExecutor(toggleCommands);
        getCommand("tpblock").setExecutor(toggleCommands);
        getCommand("tpunblock").setExecutor(toggleCommands);
        getCommand("tpaignore").setExecutor(toggleCommands);
        getCommand("tpaignoreall").setExecutor(toggleCommands);
        getCommand("tpaauto").setExecutor(toggleCommands);

        AdminCommands adminCommands = new AdminCommands(this);
        getCommand("tpahereall").setExecutor(adminCommands);
        getCommand("tpo").setExecutor(adminCommands);
        getCommand("tpohere").setExecutor(adminCommands);
        getCommand("tpareload").setExecutor(adminCommands);

        getCommand("back").setExecutor(new BackCommand(this));
    }

    public void createMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadMessagesConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public ChatUtil getChatUtil() { return chatUtil; }
    public RequestManager getRequestManager() { return requestManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public ToggleManager getToggleManager() { return toggleManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
}

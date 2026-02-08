package me.green.tpa;

import me.green.tpa.commands.*;
import me.green.tpa.listeners.DeathListener;
import me.green.tpa.listeners.MoveListener;
import me.green.tpa.listeners.WarmupListener;
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
    private EconomyManager economyManager;
    private PriceManager priceManager;
    private RefundManager refundManager;
    private HomeManager homeManager;
    private SpawnManager spawnManager;
    private RTPManager rtpManager;
    private ProviderManager providerManager;
    private TeleportRulesManager teleportRulesManager;
    private GTPSecurityManager securityManager;
    private StorageManager storageManager;

    private FileConfiguration messagesConfig;
    private File messagesFile;
    private FileConfiguration commandsConfig;
    private File commandsFile;

    @Override
    public void onDisable() {
        if (storageManager != null && storageManager.getStorage() != null) {
            storageManager.getStorage().saveToggles(toggleManager.getTpaDisabled(), toggleManager.getIgnoreAll(), toggleManager.getBlockedPlayers(), toggleManager.getIgnoredPlayers(), toggleManager.getAutoAccept());
            storageManager.getStorage().saveHomes(homeManager.getAllHomes());
            storageManager.getStorage().saveSpawns(spawnManager.getAllSpawns());
            storageManager.close();
        }
        getLogger().info("GreenTPA has been disabled!");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createMessagesConfig();
        createCommandsConfig();

        this.chatUtil = new ChatUtil(this);
        this.requestManager = new RequestManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.toggleManager = new ToggleManager();
        this.toggleManager.setDefaultAutoAccept(getConfig().getBoolean("settings.auto-accept-default", false));

        this.economyManager = new EconomyManager(this);
        this.economyManager.init();

        this.priceManager = new PriceManager(this);
        this.priceManager.load();

        this.refundManager = new RefundManager(this);

        this.homeManager = new HomeManager(this);
        this.spawnManager = new SpawnManager(this);
        this.rtpManager = new RTPManager(this);
        this.providerManager = new ProviderManager();
        this.teleportRulesManager = new TeleportRulesManager(this);
        this.securityManager = new GTPSecurityManager(this);

        this.storageManager = new StorageManager(this);
        this.storageManager.init();

        // Load data from storage
        this.storageManager.getStorage().loadToggles(toggleManager.getTpaDisabled(), toggleManager.getIgnoreAll(), toggleManager.getBlockedPlayers(), toggleManager.getIgnoredPlayers(), toggleManager.getAutoAccept());
        this.storageManager.getStorage().loadHomes(homeManager.getAllHomes());
        this.storageManager.getStorage().loadSpawns(spawnManager.getAllSpawns());

        this.teleportManager = new TeleportManager(this);

        registerCommands();
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new WarmupListener(this), this);
        getServer().getPluginManager().registerEvents(this.economyManager, this);

        getLogger().info("GreenTPA has been enabled!");
        getLogger().info("Detected Economy Provider: " + providerManager.detectProvider());
    }

    private void registerCommands() {
        TPACommand tpaCommand = new TPACommand(this);
        if (isCommandEnabled("tpa")) getCommand("tpa").setExecutor(tpaCommand);
        if (isCommandEnabled("tpahere")) getCommand("tpahere").setExecutor(tpaCommand);
        if (isCommandEnabled("tpaccept")) getCommand("tpaccept").setExecutor(tpaCommand);
        if (isCommandEnabled("tpdeny")) getCommand("tpdeny").setExecutor(tpaCommand);
        if (isCommandEnabled("tpcancel")) getCommand("tpcancel").setExecutor(tpaCommand);
        if (isCommandEnabled("tpalist")) getCommand("tpalist").setExecutor(tpaCommand);

        ToggleCommands toggleCommands = new ToggleCommands(this);
        if (isCommandEnabled("tptoggle")) getCommand("tptoggle").setExecutor(toggleCommands);
        if (isCommandEnabled("tpblock")) getCommand("tpblock").setExecutor(toggleCommands);
        if (isCommandEnabled("tpunblock")) getCommand("tpunblock").setExecutor(toggleCommands);
        if (isCommandEnabled("tpaignore")) getCommand("tpaignore").setExecutor(toggleCommands);
        if (isCommandEnabled("tpaignoreall")) getCommand("tpaignoreall").setExecutor(toggleCommands);
        if (isCommandEnabled("tpaauto")) getCommand("tpaauto").setExecutor(toggleCommands);

        AdminCommands adminCommands = new AdminCommands(this);
        if (isCommandEnabled("tpahereall")) getCommand("tpahereall").setExecutor(adminCommands);
        if (isCommandEnabled("tpo")) getCommand("tpo").setExecutor(adminCommands);
        if (isCommandEnabled("tpohere")) getCommand("tpohere").setExecutor(adminCommands);
        if (isCommandEnabled("tpareload")) getCommand("tpareload").setExecutor(adminCommands);

        if (isCommandEnabled("back")) getCommand("back").setExecutor(new BackCommand(this));

        RTPCommand rtpCommand = new RTPCommand(this);
        if (isCommandEnabled("rtp")) getCommand("rtp").setExecutor(rtpCommand);

        HomeCommand homeCommand = new HomeCommand(this);
        if (isCommandEnabled("home")) getCommand("home").setExecutor(homeCommand);
        if (isCommandEnabled("homes")) getCommand("homes").setExecutor(homeCommand);
        if (isCommandEnabled("sethome")) getCommand("sethome").setExecutor(homeCommand);
        if (isCommandEnabled("delhome")) getCommand("delhome").setExecutor(homeCommand);

        SpawnCommand spawnCommand = new SpawnCommand(this);
        if (isCommandEnabled("spawn")) getCommand("spawn").setExecutor(spawnCommand);
        if (isCommandEnabled("setspawn")) getCommand("setspawn").setExecutor(spawnCommand);
        if (isCommandEnabled("delspawn")) getCommand("delspawn").setExecutor(spawnCommand);

        getCommand("greentpa").setExecutor(new GreenTPACommand(this));
    }

    private boolean isCommandEnabled(String name) {
        return commandsConfig.getBoolean("commands." + name + ".enabled", true);
    }

    public void createMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void createCommandsConfig() {
        commandsFile = new File(getDataFolder(), "commands.yml");
        if (!commandsFile.exists()) {
            saveResource("commands.yml", false);
        }
        commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);
    }

    public void reloadMessagesConfig() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadCommandsConfig() {
        commandsConfig = YamlConfiguration.loadConfiguration(commandsFile);
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public ChatUtil getChatUtil() { return chatUtil; }
    public RequestManager getRequestManager() { return requestManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public ToggleManager getToggleManager() { return toggleManager; }
    public TeleportManager getTeleportManager() { return teleportManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public PriceManager getPriceManager() { return priceManager; }
    public RefundManager getRefundManager() { return refundManager; }
    public HomeManager getHomeManager() { return homeManager; }
    public SpawnManager getSpawnManager() { return spawnManager; }
    public RTPManager getRtpManager() { return rtpManager; }
    public ProviderManager getProviderManager() { return providerManager; }
    public TeleportRulesManager getTeleportRulesManager() { return teleportRulesManager; }
    public GTPSecurityManager getSecurityManager() { return securityManager; }
    public StorageManager getStorageManager() { return storageManager; }
}

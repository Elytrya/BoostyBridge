package onevnl.ru.elytrya.api;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import onevnl.ru.elytrya.database.Database;
import onevnl.ru.elytrya.database.MySQL;
import onevnl.ru.elytrya.database.SQLite;

public class BoostyClient {

    private final JavaPlugin plugin;
    private final HttpClient httpClient;
    private final Gson gson;

    private MessageManager messageManager;
    private AuthManager authManager;
    private BlogManager blogManager;
    private Database database;
    
    private final Map<UUID, PendingLink> pendingLinks;
    private org.bukkit.scheduler.BukkitTask syncTask;

    public BoostyClient(JavaPlugin plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.pendingLinks = new ConcurrentHashMap<>();

        loadManagers();
    }

    private void loadManagers() {
        this.messageManager = new MessageManager(plugin);
        this.authManager = new AuthManager(this);
        this.blogManager = new BlogManager(this);

        if (this.database != null) {
            this.database.disconnect();
        }

        String dbType = plugin.getConfig().getString("database.type", "SQLITE").toUpperCase();
        if (dbType.equals("MYSQL")) {
            this.database = new MySQL(plugin);
        } else {
            this.database = new SQLite(plugin);
        }
        this.database.connect();

        if (syncTask != null) {
            syncTask.cancel();
        }
        long interval = plugin.getConfig().getLong("sync.interval_minutes", 60) * 60 * 20L;
        syncTask = new onevnl.ru.elytrya.tasks.SubscriptionSyncTask(this).runTaskTimerAsynchronously(plugin, interval, interval);
    }

    public void reload() {
        plugin.reloadConfig();
        loadManagers();
        pendingLinks.clear();
    }

    public void disable() {
        if (syncTask != null) {
            syncTask.cancel();
        }
        if (database != null) {
            database.disconnect();
        }
    }

    public void debug(String message) {
        if (plugin.getConfig().getBoolean("debug", false)) {
            plugin.getLogger().info("[DEBUG] " + message);
        }
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public Gson getGson() {
        return gson;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public BlogManager getBlogManager() {
        return blogManager;
    }

    public Database getDatabase() {
        return database;
    }

    public Map<UUID, PendingLink> getPendingLinks() {
        return pendingLinks;
    }
}
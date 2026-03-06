package onevnl.ru.elytrya;

import org.bukkit.plugin.java.JavaPlugin;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.commands.BoostyCommand;
import onevnl.ru.elytrya.listeners.ChatListener;

public class BoostyBridge extends JavaPlugin {

    private BoostyClient boostyClient;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.boostyClient = new BoostyClient(this);

        getCommand("boosty").setExecutor(new BoostyCommand(this.boostyClient));
        getServer().getPluginManager().registerEvents(new ChatListener(this.boostyClient), this);

        boostyClient.getAuthManager().checkAndRefreshToken().thenAccept(v -> {
            boostyClient.getBlogManager().getBlogStats().whenComplete((stats, ex) -> {
                if (ex != null) {
                    getLogger().severe("Boosty API Error: " + ex.getMessage());
                    ex.printStackTrace();
                } else if (stats != null) {
                    getLogger().info("Boosty stats loaded successfully.");
                } else {
                    getLogger().warning("Failed to load Boosty stats.");
                }
            });
        });
    }

    @Override
    public void onDisable() {
        if (this.boostyClient != null) {
            this.boostyClient.disable();
        }
    }

    public BoostyClient getBoostyClient() {
        return boostyClient;
    }
}
package onevnl.ru.elytrya.api.managers;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DiscordManager {

    private final JavaPlugin plugin;
    private final boolean enabled;
    private final String webhookUrl;
    private final String embedColor;
    private final String messageTemplate;

    public DiscordManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("discord.enabled", false);
        this.webhookUrl = plugin.getConfig().getString("discord.webhook_url", "");
        this.embedColor = plugin.getConfig().getString("discord.embed_color", "#ff8c00");
        this.messageTemplate = plugin.getConfig().getString("discord.message", "🎉 Игрок **{player}** оформил подписку уровня **{level}**!");
    }

    public void sendWebhook(String playerName, String levelName) {
        if (!enabled || webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.contains("твой_вебхук")) {
            return;
        }

        if (levelName == null || levelName.equalsIgnoreCase("none")) {
            return;
        }

        String content = messageTemplate.replace("{player}", playerName).replace("{level}", levelName);

        JsonObject embed = new JsonObject();
        embed.addProperty("description", content);
        
        try {
            embed.addProperty("color", Integer.parseInt(embedColor.replace("#", ""), 16));
        } catch (Exception e) {
            embed.addProperty("color", 16747520); 
        }

        JsonArray embeds = new JsonArray();
        embeds.add(embed);

        JsonObject payload = new JsonObject();
        payload.add("embeds", embeds);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();

        java.net.http.HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        plugin.getLogger().warning("Failed to send Discord webhook. Response: " + response.body());
                    }
                });
    }
}
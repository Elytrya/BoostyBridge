package onevnl.ru.elytrya.api.managers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.ConfigurationSection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import onevnl.ru.elytrya.api.BoostyClient;

public class DiscordManager {
    private final BoostyClient client;
    private final HttpClient httpClient;

    public DiscordManager(BoostyClient client) {
        this.client = client;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void sendNotification(String type, String playerName, String boostyName, String levelName) {
        ConfigurationSection config = client.getPlugin().getConfig().getConfigurationSection("discord");
        if (config == null || !config.getBoolean("enabled", false)) return;

        String webhookUrl = config.getString("webhook_url", "");
        if (webhookUrl.isEmpty() || webhookUrl.contains("YOUR_WEBHOOK")) return;

        ConfigurationSection event = config.getConfigurationSection(type);
        if (event == null || !event.getBoolean("enabled", true)) return;

        String description = event.getString("message", "")
                .replace("{player}", playerName)
                .replace("{boosty_name}", boostyName != null ? boostyName : "N/A")
                .replace("{level}", levelName != null ? levelName : "none");

        JsonObject embed = new JsonObject();
        embed.addProperty("title", event.getString("title", "Notification"));
        embed.addProperty("description", description);
        
        try {
            String colorHex = config.getString("embed_color", "#FFB6C1").replace("#", "");
            embed.addProperty("color", Integer.parseInt(colorHex, 16));
        } catch (Exception e) {
            embed.addProperty("color", 16758465);
        }

        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        JsonObject payload = new JsonObject();
        payload.add("embeds", embeds);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
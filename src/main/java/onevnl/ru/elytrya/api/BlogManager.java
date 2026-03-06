package onevnl.ru.elytrya.api;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

public class BlogManager {

    private final BoostyClient client;
    private String blogHref;

    public BlogManager(BoostyClient client) {
        this.client = client;
    }

    public CompletableFuture<String> getBlogHref() {
        if (this.blogHref != null) {
            return CompletableFuture.completedFuture(this.blogHref);
        }

        AuthManager auth = client.getAuthManager();
        JsonObject cookieData = new JsonObject();
        cookieData.addProperty("accessToken", auth.getAccessToken());
        cookieData.addProperty("refreshToken", auth.getRefreshToken());
        cookieData.addProperty("expiresAt", String.valueOf(auth.getExpiresAt()));
        cookieData.addProperty("isEmptyUser", "0");
        cookieData.addProperty("redirectAppId", "web");
        cookieData.addProperty("_clientId", auth.getClientId());

        String encodedCookie = URLEncoder.encode(client.getGson().toJson(cookieData), StandardCharsets.UTF_8).replace("+", "%20");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://boosty.to"))
                .header("Cookie", "auth=" + encodedCookie)
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        return client.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    Pattern pattern = Pattern.compile("data-test-id=\"MAINFEED:ownBlogButton\"[^>]*href=\"([^\"]+)\"");
                    Matcher matcher = pattern.matcher(response.body());
                    if (matcher.find()) {
                        String href = matcher.group(1);
                        if (href.startsWith("/")) {
                            href = href.substring(1);
                        }
                        this.blogHref = href;
                        return this.blogHref;
                    }
                    return null;
                });
    }

    public CompletableFuture<JsonObject> getBlogStats() {
        return getBlogHref().thenCompose(href -> {
            if (href == null) {
                return CompletableFuture.completedFuture(null);
            }

            AuthManager auth = client.getAuthManager();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.boosty.to/v1/blog/stat/" + href + "/current"))
                    .header("Authorization", "Bearer " + auth.getAccessToken())
                    .header("X-Currency", "RUB")
                    .header("X-Locale", "ru_RU")
                    .header("X-App", "web")
                    .GET()
                    .build();

            return client.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            return client.getGson().fromJson(response.body(), JsonObject.class);
                        }
                        return null;
                    });
        });
    }

    private CompletableFuture<JsonObject> searchSubscriber(String href, String targetName, int offset, int limit) {
        AuthManager auth = client.getAuthManager();
        String url = "https://api.boosty.to/v1/blog/" + href + "/subscribers?limit=" + limit + "&offset=" + offset;
        client.debug("Fetching from: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + auth.getAccessToken())
                .header("X-App", "web")
                .GET()
                .build();

        return client.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenCompose(response -> {
                    client.debug("Response code: " + response.statusCode());
                    if (response.statusCode() == 200) {
                        JsonObject json = client.getGson().fromJson(response.body(), JsonObject.class);
                        if (json.has("data")) {
                            com.google.gson.JsonArray data = json.getAsJsonArray("data");

                            if (data.size() == 0) {
                                client.debug("No more subscribers found. Reached end of list at offset " + offset);
                                return CompletableFuture.completedFuture(null);
                            }

                            for (com.google.gson.JsonElement el : data) {
                                JsonObject sub = el.getAsJsonObject();
                                if (sub.has("name")) {
                                    String subName = sub.get("name").getAsString();
                                    if (subName.equalsIgnoreCase(targetName)) {
                                        client.debug("Match found at offset " + offset + "!");
                                        return CompletableFuture.completedFuture(sub);
                                    }
                                }
                            }

                            client.debug("Not found in current chunk. Fetching next " + limit + " subscribers...");
                            return searchSubscriber(href, targetName, offset + limit, limit);
                        }
                        client.debug("No 'data' element in JSON response.");
                        return CompletableFuture.completedFuture(null);
                    } else {
                        client.debug("Failed API response: " + response.body());
                        return CompletableFuture.completedFuture(null);
                    }
                });
    }

    public CompletableFuture<JsonObject> getSubscriberData(String name) {
        client.debug("Starting subscriber check for: '" + name + "'");
        return getBlogHref().thenCompose(href -> {
            if (href == null) {
                client.debug("Blog href is null, cannot check subscribers.");
                return CompletableFuture.completedFuture(null);
            }
            return searchSubscriber(href, name, 0, 50);
        });
    }

    private CompletableFuture<Void> fetchAllSubscribers(String href, int offset, int limit, java.util.Map<String, String> result) {
        AuthManager auth = client.getAuthManager();
        String url = "https://api.boosty.to/v1/blog/" + href + "/subscribers?limit=" + limit + "&offset=" + offset;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Authorization", "Bearer " + auth.getAccessToken()).header("X-App", "web").GET().build();

        return client.getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenCompose(response -> {
            if (response.statusCode() == 200) {
                JsonObject json = client.getGson().fromJson(response.body(), JsonObject.class);
                if (json.has("data")) {
                    com.google.gson.JsonArray data = json.getAsJsonArray("data");
                    if (data.size() == 0) return CompletableFuture.completedFuture(null);

                    for (com.google.gson.JsonElement el : data) {
                        JsonObject sub = el.getAsJsonObject();
                        if (sub.has("name")) {
                            String subName = sub.get("name").getAsString().toLowerCase();
                            String levelName = "none";
                            if (sub.has("level") && !sub.get("level").isJsonNull()) {
                                JsonObject levelObj = sub.getAsJsonObject("level");
                                if (levelObj.has("name") && !levelObj.get("name").isJsonNull()) {
                                    levelName = levelObj.get("name").getAsString();
                                }
                            }
                            result.put(subName, levelName);
                        }
                    }
                    return fetchAllSubscribers(href, offset + limit, limit, result);
                }
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    public CompletableFuture<java.util.Map<String, String>> getAllSubscribersMap() {
        return getBlogHref().thenCompose(href -> {
            if (href == null) return CompletableFuture.completedFuture(new java.util.concurrent.ConcurrentHashMap<>());
            java.util.Map<String, String> map = new java.util.concurrent.ConcurrentHashMap<>();
            return fetchAllSubscribers(href, 0, 50, map).thenApply(v -> map);
        });
    }
}
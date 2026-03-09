package onevnl.ru.elytrya.hooks;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.models.BoostyUser;

public class PlaceholderProcessor extends PlaceholderExpansion {

    private final BoostyClient client;

    public PlaceholderProcessor(BoostyClient client) {
        this.client = client;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "boosty";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Elytrya";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; 
    }

    @Override
        public String onRequest(OfflinePlayer player, @NotNull String params) {
            if (player == null) return "";

            BoostyUser user = client.getDatabase().getUser(player.getUniqueId());
            //глобал ^-^
            if (params.equalsIgnoreCase("global_subscribers")) { // %boosty_global_subscribers%
                        return String.valueOf(client.getDatabase().getActiveSubscribersCount());
            }


            //локал ^-^
            if (params.equalsIgnoreCase("level")) { // %boosty_level%
                return (user != null && user.levelName() != null && !user.levelName().equalsIgnoreCase("none")) ? user.levelName() : "None";
            }

            if (params.equalsIgnoreCase("name")) { // %boosty_name%
                return (user != null && user.boostyName() != null) ? user.boostyName() : "None";
            }

            if (params.equalsIgnoreCase("is_linked")) { //%boosty_is_linked%
                return user != null ? "true" : "false";
            }

            if (params.equalsIgnoreCase("has_sub")) { // %boosty_has_sub%
                return (user != null && user.levelName() != null && !user.levelName().equalsIgnoreCase("none")) ? "true" : "false";
            }

            return null;
        }
}
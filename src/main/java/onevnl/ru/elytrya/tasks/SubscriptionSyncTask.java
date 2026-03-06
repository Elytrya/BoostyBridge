package onevnl.ru.elytrya.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.BoostyUser;

import java.util.List;
import java.util.Map;

public class SubscriptionSyncTask extends BukkitRunnable {

    private final BoostyClient client;

    public SubscriptionSyncTask(BoostyClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        client.debug("Starting subscription sync task...");

        client.getBlogManager().getAllSubscribersMap().thenAccept(subscribers -> {
            List<BoostyUser> dbUsers = client.getDatabase().getAllUsers();
            client.debug("Loaded " + dbUsers.size() + " users from DB and " + subscribers.size() + " from Boosty.");

            for (BoostyUser user : dbUsers) {
                String boostyName = user.boostyName().toLowerCase();
                String currentLevel = user.levelName();

                if (!subscribers.containsKey(boostyName)) {
                    client.debug("User " + user.playerName() + " lost their subscription!");
                    takeRewards(user.playerName(), user.boostyName(), currentLevel);
                    client.getDatabase().removeLink(user.uuid());
                } else {
                    String newLevel = subscribers.get(boostyName);
                    if (!currentLevel.equals(newLevel)) {
                        client.debug("User " + user.playerName() + " changed level from " + currentLevel + " to " + newLevel);
                        takeRewards(user.playerName(), user.boostyName(), currentLevel);
                        giveRewards(user.playerName(), user.boostyName(), newLevel);
                        client.getDatabase().updateLevel(user.uuid(), newLevel);
                    }
                }
            }
            client.debug("Sync task completed.");
        }).exceptionally(ex -> {
            client.debug("Error during sync task: " + ex.getMessage());
            return null;
        });
    }

    private void takeRewards(String playerName, String boostyName, String levelName) {
        Bukkit.getScheduler().runTask(client.getPlugin(), () -> {
            List<String> commands = client.getPlugin().getConfig().getStringList("rewards." + levelName + ".take");
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", playerName).replace("%boosty_name%", boostyName));
            }
        });
    }

    private void giveRewards(String playerName, String boostyName, String levelName) {
        Bukkit.getScheduler().runTask(client.getPlugin(), () -> {
            List<String> commands = client.getPlugin().getConfig().getStringList("rewards." + levelName + ".give");
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", playerName).replace("%boosty_name%", boostyName));
            }
        });
    }
}
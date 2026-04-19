package onevnl.ru.elytrya.commands.subcommands;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.managers.MessageManager;
import onevnl.ru.elytrya.models.PendingLink;

public class LinkSubCommand implements SubCommand {

    private final BoostyClient client;

    public LinkSubCommand(BoostyClient client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "link";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager msg = client.getMessageManager();

        if (!(sender instanceof Player player)) {
            sender.sendMessage("only for players <3");
            return;
        }

        if (args.length < 2) {
            player.sendMessage(msg.getMessage("link_usage"));
            return;
        }

        String boostyName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        client.debug("Player " + player.getName() + " trying to link Boosty account: '" + boostyName + "'");

        if (client.getDatabase().getBoostyName(player.getUniqueId()) != null) {
            player.sendMessage(msg.getMessage("link_already_yours"));
            return;
        }

        if (client.getDatabase().isBoostyNameLinked(boostyName)) {
            player.sendMessage(msg.getMessage("link_already_linked"));
            return;
        }

        client.getBlogManager().getSubscriberData(boostyName).thenAccept(subData -> {
            if (subData != null) {
                String levelName = "none";
                if (subData.has("level") && !subData.get("level").isJsonNull()) {
                    JsonObject levelObj = subData.getAsJsonObject("level");
                    if (levelObj.has("name") && !levelObj.get("name").isJsonNull()) {
                        levelName = levelObj.get("name").getAsString();
                    }
                }

                var pluginConfig = client.getPlugin().getConfig();
                boolean useDM = pluginConfig.getBoolean("dm_verification.enabled", false);

                String userId = null;
                if (subData.has("user") && subData.getAsJsonObject("user").has("id")) {
                    userId = subData.getAsJsonObject("user").get("id").getAsString();
                } else if (subData.has("id")) {
                    userId = subData.get("id").getAsString();
                }
                client.debug("Subscriber userId for DM: " + (userId != null ? userId : "not found"));

                if (useDM && userId != null) {
                    final String finalBoostyName = boostyName;
                    final String finalLevelName = levelName;
                    final Player finalPlayer = player;
                    final String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));

                    client.getDmManager().sendVerificationCode(userId, code, player.getName())
                            .thenAccept(success -> {
                                if (success) {
                                    client.getPendingLinks().put(finalPlayer.getUniqueId(),
                                            new PendingLink(finalBoostyName, finalLevelName, code, "dm"));
                                    finalPlayer.sendMessage(msg.getMessage("link_dm_prompt"));
                                    client.debug("DM verification code sent successfully for " + finalBoostyName);
                                } else {
                                    finalPlayer.sendMessage(msg.getMessage("link_error"));
                                }
                            });
                    return;
                }

                boolean hasEmail = subData.has("email") && !subData.get("email").isJsonNull() && !subData.get("email").getAsString().isEmpty();
                boolean verifyEmail = pluginConfig.getBoolean("verify_email", true);

                if (hasEmail && verifyEmail) {
                    String correctEmail = subData.get("email").getAsString();
                    client.debug("Subscriber has email. Adding to verification cache...");
                    client.getPendingLinks().put(player.getUniqueId(),
                            new PendingLink(boostyName, levelName, correctEmail, "email"));
                    player.sendMessage(msg.getMessage("link_email_prompt"));
                } else {
                    client.debug("No verification required. Linking immediately.");
                    client.getDatabase().saveLink(player.getUniqueId(), player.getName(), boostyName, levelName);
                    player.sendMessage(msg.getMessage("link_success").replace("%name%", boostyName));
                    msg.broadcastCongratulation(player.getName(), levelName);
                    client.getDiscordManager().sendNotification("subscription", player.getName(), boostyName, levelName);
                    executeRewards(player, boostyName, levelName);
                }
            } else {
                client.debug("Account '" + boostyName + "' not found in subscribers.");
                player.sendMessage(msg.getMessage("link_not_found").replace("%name%", boostyName));
            }
        }).exceptionally(ex -> {
            client.debug("Error during linking: " + ex.getMessage());
            player.sendMessage(msg.getMessage("link_error"));
            ex.printStackTrace();
            return null;
        });
    }

    private void executeRewards(Player player, String boostyName, String levelName) {
        client.getPlugin().getServer().getScheduler().runTask(client.getPlugin(), () -> {
            List<String> commands = client.getPlugin().getConfig().getStringList("rewards." + levelName + ".give");
            if (commands != null && !commands.isEmpty()) {
                for (String cmd : commands) {
                    String finalCmd = cmd.replace("%player%", player.getName()).replace("%boosty_name%", boostyName);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
                    client.debug("Executed reward command: " + finalCmd);
                }
            } else {
                client.debug("No rewards found for level: " + levelName);
            }
        });
    }
}
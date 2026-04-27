package onevnl.ru.elytrya.commands.subcommands;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.managers.MessageManager;
import onevnl.ru.elytrya.models.BoostyUser;

public class AdminSubCommand implements SubCommand {
    private final BoostyClient client;

    public AdminSubCommand(BoostyClient client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager msg = client.getMessageManager();
        if (args.length < 2) {
            sender.sendMessage(msg.getMessage("error_use_admin"));
            return;
        }

        String action = args[1].toLowerCase();

        if (!sender.hasPermission("boosty.admin." + action)) {
            sender.sendMessage("§cУ вас нет прав: boosty.admin." + action);
            return;
        }

        switch (action) {
            case "unlink":
                handleUnlink(sender, args);
                break;
            case "info":
                handleInfo(sender, args);
                break;
            case "forcelink":
                handleForceLink(sender, args);
                break;
            case "forcesync":
                new ForceSyncSubCommand(client).execute(sender, args);
                break;
            default:
                sender.sendMessage(msg.getMessage("error_not_found"));
                break;
        }
    }

    private void handleUnlink(CommandSender sender, String[] args) {
        MessageManager msg = client.getMessageManager();
        if (args.length < 3) {
            sender.sendMessage(msg.getMessage("error_use_unlink"));
            return;
        }

        String targetName = args[2];
        boolean silent = args.length > 3 && args[3].equalsIgnoreCase("-s");

        BoostyUser user = client.getDatabase().getAllUsers().stream()
                .filter(u -> u.playerName().equalsIgnoreCase(targetName))
                .findFirst().orElse(null);

        if (user != null) {
            String notifyType = silent ? "unsubscription" : "admin_unlink";
            client.getDiscordManager().sendNotification(notifyType, user.playerName(), user.boostyName(), user.levelName());
            
            client.getDatabase().removeLink(user.uuid());
            
            sender.sendMessage(msg.getMessage("admin_unlink_success").replace("%player%", user.playerName()));
            if (!silent) {
                notifyUnlink(user.uuid(), sender.getName());
            }
        } else {
            sender.sendMessage(msg.getMessage("admin_unlink_not_linked").replace("%player%", targetName));
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        MessageManager msg = client.getMessageManager();
        if (args.length < 3) {
            sender.sendMessage(msg.getMessage("error_use_info"));
            return;
        }
        BoostyUser user = findUser(args[2]);
        if (user != null) {
            sender.sendMessage(msg.getMessage("admin_info_linked")
                    .replace("%player%", user.playerName())
                    .replace("%boosty%", user.boostyName())
                    .replace("%level%", user.levelName()));
        } else {
            sender.sendMessage(msg.getMessage("admin_info_not_linked").replace("%player%", args[2]));
        }
    }

    private void handleForceLink(CommandSender sender, String[] args) {
        MessageManager msg = client.getMessageManager();
        if (args.length < 4) {
            sender.sendMessage(msg.getMessage("error_use_forcelink"));
            return;
        }

        String targetName = args[2];
        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");
        int boostyNameEnd = silent ? args.length - 1 : args.length;
        String boostyName = String.join(" ", Arrays.copyOfRange(args, 3, boostyNameEnd));

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        String finalName = target.getName() != null ? target.getName() : targetName;

        client.getBlogManager().getSubscriberData(boostyName).thenAccept(subData -> {
            String levelName = extractLevel(subData);

            client.getDatabase().saveLink(target.getUniqueId(), finalName, boostyName, levelName);
            
            String notifyType = silent ? "subscription" : "admin_forcelink";
            client.getDiscordManager().sendNotification(notifyType, finalName, boostyName, levelName);
            
            sender.sendMessage(msg.getMessage("admin_forcelink_success")
                    .replace("%player%", finalName).replace("%boosty%", boostyName));
            
            if (!silent) {
                if (target.isOnline()) {
                    target.getPlayer().sendMessage(msg.getMessage("admin_notify_forcelinked")
                            .replace("%admin%", sender.getName()).replace("%boosty%", boostyName));
                }
                msg.broadcastCongratulation(finalName, levelName);
            }
            
            executeRewards(finalName, boostyName, levelName);

        }).exceptionally(ex -> {
            sender.sendMessage("§cError: " + ex.getMessage());
            return null;
        });
    }

    private BoostyUser findUser(String name) {
        return client.getDatabase().getAllUsers().stream()
            .filter(u -> u.playerName().equalsIgnoreCase(name))
            .findFirst().orElse(null);
    }

    private void notifyUnlink(UUID uuid, String admin) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            p.sendMessage(client.getMessageManager().getMessage("admin_notify_unlinked").replace("%admin%", admin));
        }
    }

    private String extractLevel(JsonObject subData) {
        if (subData != null && subData.has("level") && !subData.get("level").isJsonNull()) {
            JsonObject levelObj = subData.getAsJsonObject("level");
            if (levelObj.has("name") && !levelObj.get("name").isJsonNull()) {
                return levelObj.get("name").getAsString();
            }
        }
        return "none";
    }

    private void executeRewards(String playerName, String boostyName, String levelName) {
        client.getPlugin().getServer().getScheduler().runTask(client.getPlugin(), () -> {
            List<String> commands = client.getPlugin().getConfig().getStringList("rewards." + levelName + ".give");
            if (commands == null) return;
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd
                        .replace("%player%", playerName)
                        .replace("%boosty_name%", boostyName));
            }
        });
    }
}
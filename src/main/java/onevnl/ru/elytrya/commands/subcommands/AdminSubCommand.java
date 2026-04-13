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
import onevnl.ru.elytrya.models.BoostyUser;

public class AdminSubCommand implements SubCommand {
    private final BoostyClient client;

    public AdminSubCommand(BoostyClient client) { this.client = client; }

    @Override public String getName() { return "admin"; }
    @Override public String getPermission() { return "boosty.admin"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Использование: /boosty admin <unlink|info|forcelink>");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "unlink": handleUnlink(sender, args); break;
            case "info": handleInfo(sender, args); break;
            case "forcelink": handleForceLink(sender, args); break;
            default: sender.sendMessage("Неизвестная подкоманда."); break;
        }
    }

    private void handleUnlink(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Использование: /boosty admin unlink <игрок>");
            return;
        }
        String targetName = args[2];
        BoostyUser user = findUserByName(targetName);
        if (user != null) {
            client.getDatabase().removeLink(user.uuid());
            sender.sendMessage(client.getMessageManager().getMessage("admin_unlink_success").replace("%player%", user.playerName()));
            notifyUnlink(user.uuid(), sender.getName());
        } else {
            sender.sendMessage(client.getMessageManager().getMessage("admin_unlink_not_linked").replace("%player%", targetName));
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Использование: /boosty admin info <игрок>");
            return;
        }
        BoostyUser user = findUserByName(args[2]);
        if (user != null) {
            sender.sendMessage(client.getMessageManager().getMessage("admin_info_linked")
                    .replace("%player%", user.playerName()).replace("%boosty%", user.boostyName()).replace("%level%", user.levelName()));
        } else {
            sender.sendMessage(client.getMessageManager().getMessage("admin_info_not_linked").replace("%player%", args[2]));
        }
    }

    private void handleForceLink(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("Использование: /boosty admin forcelink <игрок> <boosty_name>");
            return;
        }
        String targetName = args[2];
        String boostyName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        String finalName = target.getName() != null ? target.getName() : targetName;

        client.getBlogManager().getSubscriberData(boostyName).thenAccept(subData -> {
            String level = extractLevelName(subData);
            client.getDatabase().saveLink(target.getUniqueId(), finalName, boostyName, level);
            sender.sendMessage(client.getMessageManager().getMessage("admin_forcelink_success").replace("%player%", finalName).replace("%boosty%", boostyName));
            if (target.isOnline()) target.getPlayer().sendMessage(client.getMessageManager().getMessage("admin_notify_forcelinked").replace("%admin%", sender.getName()).replace("%boosty%", boostyName));
            client.getMessageManager().broadcastCongratulation(finalName, level);
            executeRewards(finalName, boostyName, level);
        }).exceptionally(ex -> {
            sender.sendMessage("Ошибка: " + ex.getMessage());
            return null;
        });
    }

    private BoostyUser findUserByName(String name) {
        return client.getDatabase().getAllUsers().stream().filter(u -> u.playerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private void notifyUnlink(UUID uuid, String admin) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) p.sendMessage(client.getMessageManager().getMessage("admin_notify_unlinked").replace("%admin%", admin));
    }

    private String extractLevelName(JsonObject subData) {
        if (subData != null && subData.has("level") && !subData.get("level").isJsonNull()) {
            JsonObject lvl = subData.getAsJsonObject("level");
            if (lvl.has("name") && !lvl.get("name").isJsonNull()) return lvl.get("name").getAsString();
        }
        return "none";
    }

    private void executeRewards(String playerName, String boostyName, String levelName) {
        client.getPlugin().getServer().getScheduler().runTask(client.getPlugin(), () -> {
            List<String> commands = client.getPlugin().getConfig().getStringList("rewards." + levelName + ".give");
            if (commands == null) return;
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", playerName).replace("%boosty_name%", boostyName));
            }
        });
    }
}
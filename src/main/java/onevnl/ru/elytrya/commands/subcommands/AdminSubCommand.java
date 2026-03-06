package onevnl.ru.elytrya.commands.subcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import onevnl.ru.elytrya.api.BoostyClient;
import onevnl.ru.elytrya.api.BoostyUser;
import onevnl.ru.elytrya.api.MessageManager;

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
        return "boosty.admin";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager msg = client.getMessageManager();

        if (args.length < 2) {
            sender.sendMessage("Использование: /boosty admin <unlink|info|forcelink>");
            return;
        }

        String action = args[1].toLowerCase();

        if (action.equals("unlink")) {
            if (args.length < 3) {
                sender.sendMessage("Использование: /boosty admin unlink <игрок>");
                return;
            }
            String targetName = args[2];
            BoostyUser user = client.getDatabase().getAllUsers().stream()
                    .filter(u -> u.playerName().equalsIgnoreCase(targetName))
                    .findFirst().orElse(null);

            if (user != null) {
                client.getDatabase().removeLink(user.uuid());
                sender.sendMessage(msg.getMessage("admin_unlink_success").replace("%player%", user.playerName()));
                
                Player targetPlayer = Bukkit.getPlayer(user.uuid());
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    targetPlayer.sendMessage(msg.getMessage("admin_notify_unlinked")
                            .replace("%admin%", sender.getName()));
                }
            } else {
                sender.sendMessage(msg.getMessage("admin_unlink_not_linked").replace("%player%", targetName));
            }
        } 
        else if (action.equals("info")) {
            if (args.length < 3) {
                sender.sendMessage("Использование: /boosty admin info <игрок>");
                return;
            }
            String targetName = args[2];
            BoostyUser user = client.getDatabase().getAllUsers().stream()
                    .filter(u -> u.playerName().equalsIgnoreCase(targetName))
                    .findFirst().orElse(null);

            if (user != null) {
                sender.sendMessage(msg.getMessage("admin_info_linked")
                        .replace("%player%", user.playerName())
                        .replace("%boosty%", user.boostyName())
                        .replace("%level%", user.levelName()));
            } else {
                sender.sendMessage(msg.getMessage("admin_info_not_linked").replace("%player%", targetName));
            }
        }
        else if (action.equals("forcelink")) {
            if (args.length < 4) {
                sender.sendMessage("Использование: /boosty admin forcelink <игрок> <boosty_name>");
                return;
            }
            String targetName = args[2];
            String boostyName = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            String finalPlayerName = target.getName() != null ? target.getName() : targetName;

            client.getBlogManager().getSubscriberData(boostyName).thenAccept(subData -> {
                String levelName = "none";
                if (subData != null && subData.has("level") && !subData.get("level").isJsonNull()) {
                    JsonObject levelObj = subData.getAsJsonObject("level");
                    if (levelObj.has("name") && !levelObj.get("name").isJsonNull()) {
                        levelName = levelObj.get("name").getAsString();
                    }
                }
                
                client.getDatabase().saveLink(target.getUniqueId(), finalPlayerName, boostyName, levelName);
                sender.sendMessage(msg.getMessage("admin_forcelink_success")
                        .replace("%player%", finalPlayerName)
                        .replace("%boosty%", boostyName));
                
                if (target.isOnline() && target.getPlayer() != null) {
                    target.getPlayer().sendMessage(msg.getMessage("admin_notify_forcelinked")
                            .replace("%admin%", sender.getName())
                            .replace("%boosty%", boostyName));
                }

                msg.broadcastCongratulation(finalPlayerName, levelName);
                executeRewards(finalPlayerName, boostyName, levelName);
                
            }).exceptionally(ex -> {
                sender.sendMessage("Ошибка при принудительной привязке: " + ex.getMessage());
                return null;
            });
        }
    }

    private void executeRewards(String playerName, String boostyName, String levelName) {
        client.getPlugin().getServer().getScheduler().runTask(client.getPlugin(), () -> {
            List<String> commands = client.getPlugin().getConfig().getStringList("rewards." + levelName + ".give");
            if (commands != null && !commands.isEmpty()) {
                for (String cmd : commands) {
                    String finalCmd = cmd.replace("%player%", playerName).replace("%boosty_name%", boostyName);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
                    client.debug("Executed reward command (forcelink): " + finalCmd);
                }
            } else {
                client.debug("No rewards found for level: " + levelName);
            }
        });
    }
}